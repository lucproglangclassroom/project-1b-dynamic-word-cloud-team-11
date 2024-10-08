package hellotest

object ConsoleCloudObserver extends WordCloudObserver {
  def updateCloud(wordCloud: Map[String, Int]): Unit = {
    if (wordCloud.isEmpty) {
      println("No words meet the frequency or length criteria.")
    } else {
      val sortedCloud = wordCloud.toSeq.sortBy(-_._2)
      val formattedOutput = sortedCloud.map { case (word, count) => s"$word: $count" }.mkString(", ")
      println(s"Word Cloud: $formattedOutput")
    }
  }
}
