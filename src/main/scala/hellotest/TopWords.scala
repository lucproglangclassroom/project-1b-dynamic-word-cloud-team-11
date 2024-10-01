package hellotest

import scala.io.{BufferedSource, Source, StdIn}
import scala.language.unsafeNulls
import org.apache.commons.collections4.queue.CircularFifoQueue
import mainargs._
import org.slf4j.LoggerFactory

object TopWords:

  val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    SigPipeHandler.install()
    val _ = ParserForMethods(this).runOrExit(args.toIndexedSeq) // Explicitly discard return value
  }

  @main
  def run(
    @arg(short = 'c', doc = "size of the sliding word cloud") cloudSize: Int = 3,
    @arg(short = 'l', doc = "minimum word length to be considered") minLength: Int = 2,
    @arg(short = 'w', doc = "number of words to scan (window size)") windowSize: Int = 5,
    @arg(short = 's', doc = "number of steps between word cloud updates") everyKSteps: Int = 2,
    @arg(short = 'f', doc = "minimum frequency for a word to be included in the cloud") minFrequency: Int = 1,
    @arg(short = 'i', doc = "ignore list file path") ignoreListFile: String = ""
  ): Unit = {

    // Load ignore list from the file if provided
    val ignoreList: Set[String] = if (ignoreListFile.nonEmpty) {
      try {
        val source: BufferedSource = Source.fromFile(ignoreListFile)
        val ignoreSet = source.getLines().map(_.toLowerCase.nn).toSet
        source.close()
        logger.info(s"Loaded ignore list from file: $ignoreListFile")
        ignoreSet
      } catch {
        case e: Exception =>
          logger.error(s"Failed to read ignore list file: $ignoreListFile", e)
          Set.empty[String]
      }
    } else {
      Set.empty[String]
    }

    val queue = new CircularFifoQueue[String](windowSize)
    var stepCounter = 0

    // Process input lines
    LazyList.continually(StdIn.readLine()).takeWhile(line => line != null && line.nonEmpty).foreach { line =>
      val words = line.split("\\s+").nn.filter(_.nn.nonEmpty)

      words.foreach { word =>
        if (word.length >= minLength) {
          queue.add(word)

          // Increment step counter
          stepCounter += 1

          // Update word cloud after every `K` steps
          if (stepCounter % everyKSteps == 0) {
            val currentWindowWords = queue.toArray(Array.ofDim[String](queue.size())).toSeq
            WordProcessor.processWords(currentWindowWords, minLength, windowSize, cloudSize, minFrequency, ignoreList, ConsoleCloudObserver)
          }
        }
      }
    }

    // Final update for leftover words
    if (stepCounter % everyKSteps != 0) {
      val currentWindowWords = queue.toArray(Array.ofDim[String](queue.size())).toSeq
      WordProcessor.processWords(currentWindowWords, minLength, windowSize, cloudSize, minFrequency, ignoreList, ConsoleCloudObserver)
    }
  }

end TopWords
