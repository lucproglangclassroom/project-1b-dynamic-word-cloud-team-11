package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class TestWordProcessor extends AnyFlatSpec {

  // Utility method to capture console output
  def captureOutput(f: => Unit): String = {
    val stream = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(stream))(f)  // Redirect console output to stream
    Option(stream.toString).getOrElse("").trim   // Safely handle possible null
  }

  "Word Processor" should "print the correct word cloud" in {
    val words = Seq("hello", "world", "hello", "scala")

    // Capture the output from the word processor
    val output = captureOutput {
      WordProcessor.processWords(words, 2, 3, 3, 1, Set.empty, ConsoleCloudObserver)
    }

    // Verify that the output contains the correct word frequencies
    output must include ("hello -> 2")
    output must include ("world -> 1")
    output must include ("scala -> 1")
  }

  it should "respect the minimum frequency" in {
    val words = Seq("hello", "world", "hello", "scala", "scala", "hello")

    // Capture the output
    val output = captureOutput {
      WordProcessor.processWords(words, 2, 3, 3, 2, Set.empty, ConsoleCloudObserver)
    }

    // Check that only words with the minimum frequency of 2 are included
    output must include ("hello -> 3")
    output must include ("scala -> 2")
    output must not include ("world -> 1")  // Should be excluded because of frequency
  }

  it should "exclude words shorter than the minimum length" in {
    val words = Seq("hi", "world", "hello", "scala")

    // Capture the output
    val output = captureOutput {
      WordProcessor.processWords(words, 4, 3, 3, 1, Set.empty, ConsoleCloudObserver)
    }

    // Verify that short words like "hi" are excluded
    output must not include ("hi -> 1")
    output must include ("world -> 1")
    output must include ("hello -> 1")
    output must include ("scala -> 1")
  }

  it should "handle blacklisted words" in {
    val words = Seq("hello", "world", "scala", "java")
    val blacklist = Set("hello", "java")

    // Capture the output
    val output = captureOutput {
      WordProcessor.processWords(words, 3, 3, 3, 1, blacklist, ConsoleCloudObserver)
    }

    // Verify that blacklisted words ("hello" and "java") are excluded
    output must not include ("hello -> 1")
    output must not include ("java -> 1")
    output must include ("world -> 1")
    output must include ("scala -> 1")
  }

  it should "handle empty input" in {
    val words = Seq.empty[String]

    // Capture the output
    val output = captureOutput {
      WordProcessor.processWords(words, 2, 3, 3, 1, Set.empty, ConsoleCloudObserver)
    }

    // Verify that no output is generated for empty input
    output must be (empty)
  }

  it should "handle a single word" in {
    val words = Seq("hello")

    // Capture the output
    val output = captureOutput {
      WordProcessor.processWords(words, 2, 3, 3, 1, Set.empty, ConsoleCloudObserver)
    }

    // Verify that the single word is processed correctly
    output must include ("hello -> 1")
  }

  it should "handle case-insensitive word counting" in {
    val words = Seq("Hello", "hello", "HELLO")

    // Capture the output
    val output = captureOutput {
      WordProcessor.processWords(words, 2, 3, 3, 1, Set.empty, ConsoleCloudObserver)
    }

    // Verify that case-insensitive counting works
    output must include ("hello -> 3")
  }

  it should "handle large input efficiently" in {
    val words = (1 to 10000).map(_ => "hello")

    // Capture the output
    val output = captureOutput {
      WordProcessor.processWords(words, 2, 3, 3, 1, Set.empty, ConsoleCloudObserver)
    }

    // Verify that the large input is processed correctly
    output must include ("hello -> 10000")
  }
}
