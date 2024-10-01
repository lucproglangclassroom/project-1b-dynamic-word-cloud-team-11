package hellotest

import org.apache.commons.collections4.queue.CircularFifoQueue
import org.slf4j.LoggerFactory
import scala.language.unsafeNulls // Disable explicit null checks for logging

object WordProcessor:

  val logger = LoggerFactory.getLogger(this.getClass)

  def processWords(
    words: Seq[String],
    minLength: Int,
    windowSize: Int,
    cloudSize: Int,
    minFrequency: Int,
    blacklist: Set[String],
    observer: WordCloudObserver
  ): Unit = {
    val queue = new CircularFifoQueue[String](windowSize)
    val wordCount = scala.collection.mutable.Map[String, Int]()

    words.foreach { word =>
      // Remove punctuation and convert the word to lowercase
      val cleanWord = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase

      // Only process words that are not in the blacklist and meet the length requirement
      if (cleanWord.length >= minLength && !blacklist.contains(cleanWord)) {
        queue.add(cleanWord)
        wordCount(cleanWord) = wordCount.getOrElse(cleanWord, 0) + 1

        // Log word processing
        logger.debug(s"Processing word: $cleanWord, current count: ${wordCount(cleanWord)}")

        // Handle sliding window eviction
        if (queue.size() > windowSize) {
          val removedWord = queue.remove()
          wordCount(removedWord) -= 1
          if (wordCount(removedWord) == 0) wordCount -= removedWord
        }

        // Filter word counts by minimum frequency
        val filteredWordCount = wordCount.filter { case (_, count) => count >= minFrequency }

        // Notify observer with only words that meet the minFrequency condition
        observer.updateCloud(filteredWordCount.toMap)
      }
    }

    // Log final word cloud
    logger.info(s"Final word cloud: ${wordCount.mkString(", ")}")
  }

end WordProcessor
