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
    val allWords = inputLines.iterator.flatMap(_.split("\\s+").nn.filter(_.nn.nonEmpty))

    val result = WordProcessor.processWords(allWords.iterator.to(Seq), minLength, windowSize, cloudSize, minFrequency)

    println(s"Processed word cloud: ${result.mkString(", ")}")
  }

end Main
