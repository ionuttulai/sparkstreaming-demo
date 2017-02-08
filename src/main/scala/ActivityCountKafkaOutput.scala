import java.text.SimpleDateFormat
import java.util
import java.util.Calendar
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}

/**
  * Created by ionuttulai on 2/3/17.
  *
  * Reads from kafka stream, aggregates and acts as a produce for an output Kafka stream
  */
object ActivityCountKafkaOutput {
  def main(args: Array[String]) {
    if (args.length < 6) {
      System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads> <outKafkaBrokers> <outputTopic>")
      System.exit(1)
    }

   //StreamingExamples.setStreamingLogLevels()

    val Array(zkQuorum, group, topics, numThreads, outBrokers, outTopic) = args
    val sparkConf = new SparkConf().setAppName("ActivityCountKafkaOutput")

    sparkConf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")

    val ssc = new StreamingContext(sparkConf, Seconds(5))
    ssc.checkpoint("checkpoint")

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)

    //filter the activity log by log type (1 = Add) and map the activity type code (third column in the CSV)
    val filtered = lines.map(l => l.split(",")).filter(m => m(4) == "1").map(m => (m(2),1))

    val counts = filtered.reduceByKeyAndWindow(_ + _, _ - _, Minutes(15), Seconds(5), 2)
    //counts.print()

    val calendar = Calendar.getInstance().getTime()
    val hourFormat = new SimpleDateFormat("yyyyMMddhhmmss")

    counts.foreachRDD( (rdd, time) => {

        val key = time.toString()
        rdd.foreachPartition( partition => {

          val props = new util.HashMap[String, Object]()
          props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, outBrokers)
          props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
          props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

          val producer = new KafkaProducer[String, String](props)

          partition.foreach(line => {

            val data = key+","+line.toString()
            System.out.println(data);
            val message = new ProducerRecord[String, String](outTopic, null, data)
            producer.send(message)

          })

          producer.close()
        })
    })

    ssc.start()
    ssc.awaitTermination()
  }
}
