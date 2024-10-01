package hellotest

import org.apache.commons.collections4.queue.CircularFifoQueue

object WordProcessor {

  def processWords(
    words: Seq[String],
    minLength: Int,
    windowSize: Int,
    cloudSize: Int,
    minFrequency: Int,
    blacklist: Set[String],
    observer: WordCloudObserver // Add observer here
  ): Unit = {
    val queue = new CircularFifoQueue[String](windowSize)
    val wordCount = scala.collection.mutable.Map[String, Int]()

    words.foreach { word =>
      // Remove punctuation and convert the word to lowercase
      val cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").nn.toLowerCase.nn

      // Only process words that are not in the blacklist and meet the length requirement
      if (cleanWord.length >= minLength && !blacklist.contains(cleanWord)) {
        queue.add(cleanWord)
        wordCount(cleanWord) = wordCount.getOrElse(cleanWord, 0) + 1

        if (queue.size() > windowSize) {
          val removedWord = queue.remove()
          wordCount(removedWord.nn) -= 1
          if (wordCount(removedWord.nn) == 0) wordCount -= removedWord.nn
        }

        // Notify observer after each update
        observer.updateCloud(wordCount.toMap)
      }
    }
  }
}
