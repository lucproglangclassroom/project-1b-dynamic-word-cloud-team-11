package hellotest

trait WordCloudObserver {
  def updateCloud(wordCloud: Map[String, Int]): Unit
}
