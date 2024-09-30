name := "TopWords"

version := "0.3"

scalaVersion := "3.3.3"

scalacOptions += "@.scalacOptions.txt"

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-collections4" % "4.4",
  "com.lihaoyi" %% "mainargs" % "0.6.3",
  "ch.qos.logback" % "logback-classic" % "1.2.11",    // For logging
  "org.scalatest" %% "scalatest" % "3.2.17" % "test"  // For testing
)

enablePlugins(JavaAppPackaging)
