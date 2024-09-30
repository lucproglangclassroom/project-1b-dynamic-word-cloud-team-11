package hellotest

import org.apache.commons.collections4.queue.CircularFifoQueue

object WordProcessor {

  def processWords(
    words: Seq[String],
    minLength: Int,
    windowSize: Int,
    cloudSize: Int,
    minFrequency: Int,
    blacklist: Set[String] // Add blacklist parameter
  ): Map[String, Int] = {
    val queue = new CircularFifoQueue[String](windowSize)
    val wordCount = scala.collection.mutable.Map[String, Int]()

    words.foreach { word =>
      if (word.length >= minLength && !blacklist.contains(word.nn)) { // Skip blacklisted words
        queue.add(word)
        wordCount(word.nn) = wordCount.getOrElse(word.nn, 0) + 1

        if (queue.size() > windowSize) {
          val removedWord = queue.remove()
          wordCount(removedWord.nn) -= 1
          if (wordCount(removedWord.nn) == 0) wordCount -= removedWord.nn
        }
      }
    }

    wordCount.filter(_._2 >= minFrequency).toSeq.sortBy(-_._2).take(cloudSize).toMap
  }
}
