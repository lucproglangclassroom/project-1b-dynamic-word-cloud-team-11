package hellotest

import org.apache.commons.collections4.queue.CircularFifoQueue

object WordProcessor {

  def processWords(
    words: Seq[String],
    minLength: Int,
    windowSize: Int,
    cloudSize: Int,
    minFrequency: Int,
    blacklist: Set[String]
  ): Map[String, Int] = {
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
      }
    }

    // Sort the word counts by frequency (descending) and filter by minimum frequency
    wordCount
      .filter(_._2 >= minFrequency)  // Only include words above min frequency
      .toSeq                         // Convert to sequence for sorting
      .sortBy(-_._2)                // Sort by frequency (descending)
      .take(cloudSize)              // Limit to the cloud size
      .toMap                         // Convert back to a map
  }
}
