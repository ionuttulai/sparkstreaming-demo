/**
  * Created by ionuttulai on 2/3/17.
  */

import org.apache.spark.{SparkConf, SparkContext};

object LocalActivityCount {
  def main(args: Array[String]): Unit = {


    val conf = new SparkConf().setAppName("LocalActivityCount").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val tf = sc.textFile(args(0))
    val splits = tf.map(l => l.split(",")).map(r => (r(3),1))
    val counts = splits.reduceByKey((x,y) => x+y)

    splits.saveAsTextFile(args(1))
    counts.saveAsTextFile(args(2))
  }
}
