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
    val _ = ParserForMethods(this).runOrExit(args.toIndexedSeq) // Explicitly discard return value
  }

  @main
  def run(
    @arg(short = 'c', doc = "size of the sliding word cloud") cloudSize: Int = 10,
    @arg(short = 'l', doc = "minimum word length to be considered") minLength: Int = 6,
    @arg(short = 'w', doc = "number of words to scan (window size)") windowSize: Int = 5,
    @arg(short = 's', doc = "number of steps between word cloud updates") everyKSteps: Int = 10,
    @arg(short = 'f', doc = "minimum frequency for a word to be included in the cloud") minFrequency: Int = 3,
    @arg(short = 'i', doc = "path to input text file") inputFile: String = "",
    @arg(short = 'b', doc = "path to blacklist file") blacklistFile: String = ""
  ): Unit = {
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
    if (blacklistFile.nonEmpty) {
      println(s"Blacklist File: $blacklistFile")
    }

    // Read input lines from the specified file or standard input
    val inputLines = if (inputFile.nonEmpty) {
      scala.io.Source.fromFile(inputFile).getLines()
    } else {
      LazyList.continually(StdIn.readLine()).takeWhile(line => line != null && line.nonEmpty)
    }

    // Read blacklisted words from the blacklist file and convert to lowercase
    val blacklist = if (blacklistFile.nonEmpty) {
      scala.io.Source.fromFile(blacklistFile).getLines().map(_.toLowerCase.nn).toSet
    } else {
      Set.empty[String]
    }

    // Process words, limiting to `windowSize`
    val allWords = inputLines.iterator
      .flatMap(_.split("\\s+").nn.filter(_.nn.nonEmpty).map(_.toLowerCase.nn))
      .take(windowSize)

    // Pass the blacklist to WordProcessor
    val result = WordProcessor.processWords(allWords.to(Seq), minLength, windowSize, cloudSize, minFrequency, blacklist)

    // Sort results by frequency before printing
    val sortedResult = result.toSeq.sortBy(-_._2).take(cloudSize)

    // Print processed word cloud
    println(s"Processed word cloud: ${sortedResult.map { case (word, count) => s"$word -> $count" }.mkString(", ")}")
  }

end Main
