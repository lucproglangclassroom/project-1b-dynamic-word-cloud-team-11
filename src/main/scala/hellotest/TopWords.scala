package hellotest

import scala.io.StdIn
import scala.language.unsafeNulls
import org.apache.commons.collections4.queue.CircularFifoQueue
import mainargs._

object Main:

  def main(args: Array[String]): Unit = {
    // Install SIGPIPE handler
    SigPipeHandler.install()

    // Run the parser
    ParserForMethods(this).runOrExit(args.toIndexedSeq)
    ()
  }

  @main
  def run(
    @arg(short = 'c', doc = "size of the sliding word cloud") cloudSize: Int = 10,
    @arg(short = 'l', doc = "minimum word length to be considered") minLength: Int = 6,
    @arg(short = 'w', doc = "size of the sliding FIFO queue") windowSize: Int = 5,
    @arg(short = 's', doc = "number of steps between word cloud updates") everyKSteps: Int = 10,
    @arg(short = 'f', doc = "minimum frequency for a word to be included in the cloud") minFrequency: Int = 3,
    @arg(short = 'i', doc = "path to input text file") inputFile: String = ""
  ): Unit = {
    val queue = new CircularFifoQueue[String](windowSize)
    val wordCount = scala.collection.mutable.Map[String, Int]()
    var stepCounter = 0
    
    // Log the parsed arguments
    println(s"Cloud Size: $cloudSize")
    println(s"Minimum Length: $minLength")
    println(s"Window Size: $windowSize")
    println(s"Every K Steps: $everyKSteps")
    println(s"Minimum Frequency: $minFrequency")
    if (inputFile.nonEmpty) {
      println(s"Input File: $inputFile")
    }

    // Read input lines from the specified file or standard input
    val inputLines = if (inputFile.nonEmpty) {
      scala.io.Source.fromFile(inputFile).getLines()
    } else {
      LazyList.continually(StdIn.readLine()).takeWhile(line => line != null && line.nonEmpty)
    }

    // Process each line
    inputLines.foreach { line =>
      val words = line.split("\\s+").filter(_ != null).filter(_.nonEmpty)

      // Debugging: print the words being processed and the current word count before this line
      println(s"Words being processed: ${words.mkString(", ")}")
      println(s"Current word count before this line: ${wordCount.mkString(", ")}")

      for (word <- words if word.length >= minLength) {
        queue.add(word)
        wordCount(word) = wordCount.getOrElse(word, 0) + 1

        // Handle sliding window (remove old word if queue is full)
        if (queue.size() > windowSize) {
          val removedWord = queue.remove()
          if (wordCount.contains(removedWord)) {
            wordCount(removedWord) -= 1
            if (wordCount(removedWord) == 0) {
              wordCount -= removedWord
            }
          }
        }
      }

      stepCounter += 1

      // Debugging: print the stepCounter
      println(s"Step Counter: $stepCounter")

      // Print the word cloud every K steps, but only after we've seen at least windowSize words
      if (stepCounter >= windowSize && stepCounter % everyKSteps == 0) {
        println(s"Current word count after processing the line: ${wordCount.mkString(", ")}")
        printWordFrequencies(wordCount, minFrequency, cloudSize)
      }
    }

    // Force print the word cloud after all input is processed
    println(s"Final word count: ${wordCount.mkString(", ")}")
    printWordFrequencies(wordCount, minFrequency, cloudSize)
  }

  // Function to print word frequencies
  def printWordFrequencies(wordCount: scala.collection.mutable.Map[String, Int], minFrequency: Int, cloudSize: Int): Unit = {
    val filteredCounts = wordCount.filter { case (_, count) => count >= minFrequency }
    if (filteredCounts.nonEmpty) {
      val output = filteredCounts.toSeq
        .sortBy(-_._2)
        .take(cloudSize)
        .map { case (word, count) => s"$word: $count" }
        .mkString(", ")
      println(output) // Output the frequencies
    } else {
      println("No words meet the minimum frequency criteria.")
    }
  }

end Main
