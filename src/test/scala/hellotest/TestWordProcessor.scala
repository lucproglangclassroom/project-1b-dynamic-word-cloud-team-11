package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TestWordProcessor extends AnyFlatSpec {

  def captureOutput(f: => Unit): String = {
    val stream = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(stream))(f)  // Redirect console output to stream
    Option(stream.toString).getOrElse("") // Provide a default empty string
  }

  def testWordProcessor(
    words: Seq[String],
    minLength: Int = 2,
    windowSize: Int = 3,
    cloudSize: Int = 3,
    minFrequency: Int = 1,
    blacklist: Set[String] = Set.empty
  ): String = captureOutput {
    WordProcessor.processWords(words, minLength, windowSize, cloudSize, minFrequency, blacklist, ConsoleCloudObserver)
  }

  "Word Processor" should "print the correct word cloud" in {
    val words = Seq("hello", "world", "hello", "scala")
    val output = testWordProcessor(words)

    output must include ("hello: 2")
    output must include ("world: 1")
    output must include ("scala: 1")
  }

  it should "handle large input efficiently" in {
    val words = (1 to 10000).map(_ => "hello")
    val output = testWordProcessor(words)

    output must include ("hello: 10000")  // Updated format
  }

  it should "handle blacklisted words from a file" in {
    val words = Seq("hello", "world", "scala", "java")
    val blacklist = Set("hello", "java")
    val output = testWordProcessor(words, blacklist = blacklist)

    output must not include ("hello: 1")
    output must not include ("java: 1")
    output must include ("world: 1")
    output must include ("scala: 1")
  }

  it should "handle empty input and empty blacklist" in {
    val words = Seq.empty[String]
    val output = testWordProcessor(words, blacklist = Set.empty[String])

    output must be (empty)
  }

}
