import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext, Minutes}
import org.apache.spark.streaming.kafka.KafkaUtils

/**
  * Created by ionuttulai on 2/3/17.
  *
  * Reads input from Kafka Stream and aggregates on a 15 minute window
  */
object ActivityCount {
  def main(args: Array[String]) {
    if (args.length < 4) {
      System.err.println("Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>")
      System.exit(1)
    }


    val Array(zkQuorum, group, topics, numThreads) = args
    val sparkConf = new SparkConf().setAppName("ActivityCount")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")

    val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)

    val filtered = lines.map(l => l.split(",")).filter(m => m(4) == "1").map(m => (m(2),1))

    val counts = filtered.reduceByKeyAndWindow(_ + _, _ - _, Minutes(15), Seconds(2), 2)
    counts.print()

    ssc.start()
    ssc.awaitTermination()
  }
}
