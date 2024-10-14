package hellotest

import scala.io.StdIn
import scala.language.unsafeNulls
import mainargs._
import org.slf4j.LoggerFactory
import scala.collection.immutable.Queue

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

    // Recursive function to update the sliding window using immutable Queue
    def updateWindow(window: Queue[String], word: String, maxSize: Int): Queue[String] = {
      val newWindow = window.enqueue(word)
      if (newWindow.size > maxSize) newWindow.dequeue._2 else newWindow
    }

    // Higher-order function for processing the word cloud output
    def processWordCloud(wordCloud: Map[String, Int]): Unit = {
      if (wordCloud.isEmpty) {
        println("No words meet the frequency or length criteria.")
      } else {
        val sortedCloud = wordCloud.toSeq.sortBy(-_._2)
        val formattedOutput = sortedCloud.map { case (word, count) => s"$word: $count" }.mkString(", ")
        println(s"Word Cloud: $formattedOutput")
      }
    }

    // Recursive function to process input lines without mutable state
    def processLines(
      inputLines: LazyList[String],
      window: Queue[String],
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
          WordProcessor.processWords(newWindow.toList, minLength, windowSize, cloudSize, minFrequency, Set.empty[String], processWordCloud)
        }

        // Recur for the remaining lines
        processLines(inputLines.tail, newWindow, newStepCounter)
      } else {
        // Final update for any leftover words
        WordProcessor.processWords(window.toList, minLength, windowSize, cloudSize, minFrequency, Set.empty[String], processWordCloud)
      }
    }

    // Start processing the lines with initial state
    processLines(inputLines, Queue.empty[String], 0)

    logger.info("TopWords processing complete.")
  }

end TopWords