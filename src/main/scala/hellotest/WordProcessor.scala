package hellotest

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
    
    def updateWordCount(wordCount: Map[String, Int], word: String): Map[String, Int] = {
      wordCount + (word -> (wordCount.getOrElse(word, 0) + 1))
    }

    def evictWord(wordCount: Map[String, Int], word: String): Map[String, Int] = {
      val updatedCount = wordCount.getOrElse(word, 0) - 1
      if (updatedCount > 0) wordCount + (word -> updatedCount)
      else wordCount - word
    }

    def processQueue(
      remainingWords: Seq[String],
      currentQueue: List[String],
      wordCount: Map[String, Int]
    ): Unit = {
      if (remainingWords.nonEmpty) {
        val word = remainingWords.head.replaceAll("[^a-zA-Z0-9]", "").toLowerCase

        if (word.length >= minLength && !blacklist.contains(word)) {
          val newQueue = (word :: currentQueue).take(windowSize)
          val updatedWordCount = updateWordCount(wordCount, word)

          // Handle sliding window eviction
          val finalWordCount = if (newQueue.size > windowSize) {
            evictWord(updatedWordCount, currentQueue.last)
          } else updatedWordCount

          // Filter word counts by minimum frequency
          val filteredWordCount = finalWordCount.filter { case (_, count) => count >= minFrequency }

          // Notify observer with only words that meet the minFrequency condition
          observer.updateCloud(filteredWordCount)

          // Process the rest of the words recursively
          processQueue(remainingWords.tail, newQueue, finalWordCount)
        } else {
          processQueue(remainingWords.tail, currentQueue, wordCount)
        }
      }
    }

    // Start processing with an empty queue and word count
    processQueue(words, List.empty, Map.empty)

  }

end WordProcessor
