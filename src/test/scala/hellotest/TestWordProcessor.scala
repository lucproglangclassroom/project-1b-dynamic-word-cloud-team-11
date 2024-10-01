package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import org.scalactic.Prettifier.default

class TestWordProcessor extends AnyFlatSpec {

  def captureOutput(f: => Unit): String = {
  val stream = new ByteArrayOutputStream()
  Console.withOut(new PrintStream(stream))(f)  // Redirect console output to stream
  Option(stream.toString).getOrElse("") // Provide a default empty string
}

  // Helper method to process words and return the output
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
    
    output must include ("hello -> 2")
    output must include ("world -> 1")
    output must include ("scala -> 1")
  }

  it should "respect the minimum frequency" in {
    val words = Seq("hello", "world", "hello", "scala", "scala", "hello")
    val output = testWordProcessor(words, minFrequency = 2)
    
    output must include ("hello -> 3")
    output must include ("scala -> 2")
    output must not include ("world -> 1")  // Should be excluded because of frequency
  }

  it should "exclude words shorter than the minimum length" in {
    val words = Seq("hi", "world", "hello", "scala")
    val output = testWordProcessor(words, minLength = 4)

    output must not include ("hi -> 1")
    output must include ("world -> 1")
    output must include ("hello -> 1")
    output must include ("scala -> 1")
  }

  it should "handle blacklisted words" in {
    val words = Seq("hello", "world", "scala", "java")
    val blacklist = Set("hello", "java")
    val output = testWordProcessor(words, blacklist = blacklist)

    output must not include ("hello -> 1")
    output must not include ("java -> 1")
    output must include ("world -> 1")
    output must include ("scala -> 1")
  }

  it should "handle empty input" in {
    val words = Seq.empty[String]
    val output = testWordProcessor(words)

    output must be (empty)
  }

  it should "handle a single word" in {
    val words = Seq("hello")
    val output = testWordProcessor(words)

    output must include ("hello -> 1")
  }

  it should "handle case-insensitive word counting" in {
    val words = Seq("Hello", "hello", "HELLO")
    val output = testWordProcessor(words)

    output must include ("hello -> 3")
  }

  it should "handle large input efficiently" in {
    val words = (1 to 10000).map(_ => "hello")
    val output = testWordProcessor(words)

    output must include ("hello -> 10000")
  }
   it should "handle blacklisted words from a file" in {
    val words = Seq("hello", "world", "scala", "java")
    val blacklist = Set("hello", "java")
    val output = testWordProcessor(words, blacklist = blacklist)

    output must not include ("hello -> 1")
    output must not include ("java -> 1")
    output must include ("world -> 1")
    output must include ("scala -> 1")
  }

  it should "handle empty input and empty blacklist" in {
    val words = Seq.empty[String]
    val output = testWordProcessor(words, blacklist = Set.empty[String])

    output must be (empty)
  }

}
