name := "TopWords"

version := "0.3"

scalaVersion := "3.3.3"

scalacOptions += "@.scalacOptions.txt"

libraryDependencies ++= Seq(
  "org.apache.commons" % "commons-collections4" % "4.4",
  "com.lihaoyi" %% "mainargs" % "0.6.3"
)

enablePlugins(JavaAppPackaging)
