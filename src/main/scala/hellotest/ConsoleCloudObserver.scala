package hellotest

object ConsoleCloudObserver extends WordCloudObserver {
  def updateCloud(wordCloud: Map[String, Int]): Unit = {
    val sortedCloud = wordCloud.toSeq.sortBy(-_._2)
    println(s"Processed word cloud: ${sortedCloud.map { case (word, count) => s"$word -> $count" }.mkString(", ")}")
  }
}
