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

    // Log the starting parameters
    logger.info(s"Starting TopWords with cloudSize=$cloudSize, minLength=$minLength, windowSize=$windowSize, everyKSteps=$everyKSteps, minFrequency=$minFrequency")

    // Read input lines from the specified file or standard input
    val inputLines = if (inputFile.nonEmpty) {
      logger.info(s"Reading input from file: $inputFile")
      scala.io.Source.fromFile(inputFile).getLines().to(LazyList)
    } else {
      logger.info("Reading input from standard input")
      LazyList.continually(StdIn.readLine()).takeWhile(line => line != null && line.nonEmpty)
    }

    // Recursive function to update the sliding window
    def updateWindow(window: Seq[String], word: String, maxSize: Int): Seq[String] = {
      val newWindow = window :+ word
      if (newWindow.size > maxSize) newWindow.drop(1) else newWindow
    }

    // Recursive function to process input lines without mutable state
    def processLines(
      inputLines: LazyList[String],
      window: Seq[String],
      stepCounter: Int
    ): Unit = {
      if (inputLines.nonEmpty) {
        val words = inputLines.head.split("\\s+").nn.filter(_.nn.length >= minLength)

        val newWindow = words.foldLeft(window) {
          (currentWindow, word) => updateWindow(currentWindow, word, windowSize)
        }

        val newStepCounter = stepCounter + words.size

        // Update word cloud every `K` steps
        if (newStepCounter % everyKSteps == 0) {
          WordProcessor.processWords(newWindow, minLength, windowSize, cloudSize, minFrequency, Set.empty[String], ConsoleCloudObserver)
        }

        // Recur for the remaining lines
        processLines(inputLines.tail, newWindow, newStepCounter)
      } else {
        // Final update for any leftover words
        WordProcessor.processWords(window, minLength, windowSize, cloudSize, minFrequency, Set.empty[String], ConsoleCloudObserver)
      }
    }

    // Start processing the lines with initial state
    processLines(inputLines, Seq.empty[String], 0)

    logger.info("TopWords processing complete.")
  }

end TopWords
