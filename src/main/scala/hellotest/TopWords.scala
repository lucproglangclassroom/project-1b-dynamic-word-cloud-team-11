package hellotest

import scala.io.StdIn
import scala.language.unsafeNulls
import org.apache.commons.collections4.queue.CircularFifoQueue
import mainargs._
import org.slf4j.LoggerFactory

object TopWords:

  private val logger = LoggerFactory.getLogger(this.getClass)

  def main(args: Array[String]): Unit = {
    SigPipeHandler.install()
    val _ = ParserForMethods(this).runOrExit(args.toIndexedSeq)
  }

  @main
  def run(
    @arg(short = 'c', doc = "size of the sliding word cloud") cloudSize: Int = 3,
    @arg(short = 'l', doc = "minimum word length to be considered") minLength: Int = 2,
    @arg(short = 'w', doc = "number of words to scan (window size)") windowSize: Int = 5,
    @arg(short = 's', doc = "number of steps between word cloud updates") everyKSteps: Int = 2,
    @arg(short = 'f', doc = "minimum frequency for a word to be included in the cloud") minFrequency: Int = 1,
    @arg(short = 'i', doc = "path to input text file") inputFile: String = ""
  ): Unit = {
    val queue = new CircularFifoQueue[String](windowSize)
    var stepCounter = 0

    // Log the starting parameters
    logger.info(s"Starting TopWords with cloudSize=$cloudSize, minLength=$minLength, windowSize=$windowSize, everyKSteps=$everyKSteps, minFrequency=$minFrequency")

    // Read input lines from the specified file or standard input
    val inputLines = if (inputFile.nonEmpty) {
      logger.info(s"Reading input from file: $inputFile")
      scala.io.Source.fromFile(inputFile).getLines()
    } else {
      logger.info("Reading input from standard input")
      LazyList.continually(StdIn.readLine()).takeWhile(line => line != null && line.nonEmpty)
    }

    // Process input lines and update the word cloud
    inputLines.foreach { line =>
      val words = line.split("\\s+").nn.filter(_.nn.nonEmpty)

      words.foreach { word =>
        if (word.length >= minLength) {
          queue.add(word)

          // Increment step counter
          stepCounter += 1

          // Update word cloud after every `K` steps
          if (stepCounter % everyKSteps == 0) {
            val currentWindowWords = queue.toArray(Array.ofDim[String](queue.size())).toSeq
            WordProcessor.processWords(currentWindowWords, minLength, windowSize, cloudSize, minFrequency, Set.empty[String], ConsoleCloudObserver)
          }
        }
      }
    }

    // Final update for leftover words
    if (stepCounter % everyKSteps != 0) {
      val currentWindowWords = queue.toArray(Array.ofDim[String](queue.size())).toSeq
      WordProcessor.processWords(currentWindowWords, minLength, windowSize, cloudSize, minFrequency, Set.empty[String], ConsoleCloudObserver)
    }

    logger.info("TopWords processing complete.")
  }

end TopWords
