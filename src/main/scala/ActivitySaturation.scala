import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}

/**
  * Created by ionuttulai on 2/3/17.
  *
  * Reads input from Kafka Stream and aggregates on a 15 minute window
  */
object ActivitySaturation {
  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>")
      System.exit(1)
    }


    val Array(zkQuorum, group, topics, numThreads) = args
    val sparkConf = new SparkConf().setAppName("ActivitySaturation")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)

    val filtered = lines.map(l => l.split(",")).filter(m => m(4) == "1").map(l => ((l(1),l(7),l(2)),1))

    //val counts = filtered.reduceByKeyAndWindow(_ + _, _ - _, Minutes(15), Seconds(2), 2)
    val counts = filtered.reduceByKey((a, b) => a+ b)
    counts.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
