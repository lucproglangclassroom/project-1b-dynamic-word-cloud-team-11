package hellotest

import scala.io.{Source, StdIn}
import scala.language.unsafeNulls
import org.apache.commons.collections4.queue.CircularFifoQueue
import mainargs._


object Main:

  def main(args: Array[String]): Unit = {
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

    // Read input lines from the specified file
    val inputLines = if (inputFile.nonEmpty) {
      scala.io.Source.fromFile(inputFile).getLines()
    } else {
      Iterator.continually(StdIn.readLine()).takeWhile(line => line != null && line.nonEmpty)
    }

    // Process each line
    inputLines.foreach { line =>
      val words = line.split("\\s+").filter(_.nonEmpty)

      for (word <- words if word.length >= minLength) {
        queue.add(word)
        wordCount(word) = wordCount.getOrElse(word, 0) + 1
      }

      stepCounter += 1
    }

    // After processing all lines, print the word frequencies
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
