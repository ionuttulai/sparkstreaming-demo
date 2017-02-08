name := "s4"

version := "1.0"

//scalaVersion := "2.12.1"
scalaVersion := "2.10.4"

fork := true

libraryDependencies ++= Seq(
  ("org.apache.spark" % "spark-core_2.10" % "1.6.0" )
    .exclude("org.apache.hadoop","hadoop-yarn-server-web-proxy")
    .exclude("org.apache.hadoop","hadoop-yarn-common")
    .exclude("org.apache.hadoop","hadoop-yarn-api")
    .exclude("org.apache.htrace","htrace-core")
    .exclude("org.apache.avro","avro-ipc")
    .exclude("com.esotericsoftware.kryo", "kryo")
    .exclude("com.google.guava", "guava")
    .exclude("org.eclipse.jetty.orbit","javax.servlet")
    .exclude("commons-beanutils","commons-beanutils")
    .exclude("commons-beanutils","commons-beanutils-core")
    .exclude("org.slf4j", "jcl-over-slf4j")
    .exclude("org.spark-project.spark", "unused"),



/*  ("org.apache.hadoop" % "hadoop-common" % "2.7.0")
    .exclude("org.apache.hadoop","hadoop-yarn-server-web-proxy")
    .exclude("org.apache.htrace","htrace-core")
    .exclude("org.apache.avro","avro-ipc")
    .exclude("com.esotericsoftware.kryo", "kryo")
    .exclude("com.google.guava", "guava")
    .exclude("org.eclipse.jetty.orbit","javax.servlet")
    .exclude("commons-beanutils","commons-beanutils")
    .exclude("commons-beanutils","commons-beanutils-core")
    .exclude("org.slf4j", "jcl-over-slf4j"),*/

  //"org.apache.spark" % "spark-sql_2.10" % "1.6.0" exclude ("org.apache.hadoop","hadoop-yarn-server-web-proxy"),
 // "org.apache.spark" % "spark-sql_2.10" % "1.6.0" exclude ("org.apache.hadoop","hadoop-yarn-server-web-proxy"),
  //"org.apache.spark" % "spark-hive_2.10" % "1.6.0" exclude ("org.apache.hadoop","hadoop-yarn-server-web-proxy"),
  //"org.apache.spark" % "spark-yarn_2.10" % "1.6.0" exclude ("org.apache.hadoop","hadoop-yarn-server-web-proxy"),


  ("org.apache.spark" % "spark-streaming_2.10" % "1.6.0" )
      .exclude("org.apache.htrace","htrace-core")
      .exclude("org.apache.avro","avro-ipc")
      .exclude("com.esotericsoftware.kryo", "kryo")
      .exclude("commons-beanutils","commons-beanutils")
      .exclude("org.spark-project.spark", "unused")
    .exclude("org.spark-project.spark", "unused"),

  ("org.apache.spark" % "spark-streaming-kafka_2.10" % "1.6.0")
      .exclude("org.apache.htrace","htrace-core")
      .exclude("org.apache.avro","avro-ipc")
      .exclude("com.esotericsoftware.kryo", "kryo")
      .exclude("commons-beanutils","commons-beanutils")
      .exclude("org.spark-project.spark", "unused")

  /*("org.apache.hbase" % "hbase-client" % "1.2.4")
    .exclude("org.apache.htrace","htrace-core")
    .exclude("org.apache.avro","avro-ipc")
    .exclude("com.esotericsoftware.kryo", "kryo")
    .exclude("commons-beanutils","commons-beanutils")
    .exclude("org.spark-project.spark", "unused")
    .exclude("org.spark-project.spark", "unused"),

 /* ("org.apache.hbase" % "hbase-server" % "0.98.0-hadoop2")
    .exclude("org.apache.hadoop","hadoop-yarn-server-web-proxy")
    .exclude("org.apache.htrace","htrace-core")
    .exclude("org.apache.avro","avro-ipc")
    .exclude("com.esotericsoftware.kryo", "kryo")
    .exclude("com.google.guava", "guava")
    .exclude("org.eclipse.jetty.orbit","javax.servlet")
    .exclude("commons-beanutils","commons-beanutils")
    .exclude("commons-beanutils","commons-beanutils-core")
    .exclude("org.slf4j", "jcl-over-slf4j")
    .exclude("org.spark-project.spark", "unused")
    .exclude("org.spark-project.spark", "unused"),*/

  ("org.apache.hbase" % "hbase-common" % "1.2.4")*/


)

lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0-SNAPSHOT"
)

lazy val app = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "my-app-test"
  ).
  enablePlugins(AssemblyPlugin)

resolvers in Global ++= Seq(
  "Sbt plugins"                   at "https://dl.bintray.com/sbt/sbt-plugin-releases",
 // "Maven Central Server"          at "http://repo1.maven.org/maven2",
  "TypeSafe Repository Releases"  at "http://repo.typesafe.com/typesafe/releases/",
  "TypeSafe Repository Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"
)

assemblyMergeStrategy in assembly := {
  case PathList("org.apache.hadoop")         => MergeStrategy.first
  //case PathList("org.apache.spark","unused")         => MergeStrategy.first
  case PathList("org.slf4j", "impl", xs @ _*) => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}