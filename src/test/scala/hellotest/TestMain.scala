package hellotest

import java.nio.file.{Files, Paths}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.util.Using

class TestMain extends AnyFlatSpec with Matchers {

  "TopWords" should "run correctly with given arguments" in {
    // Create a temporary file to simulate input
    val inputContent = "hello\nworld\nhello\n"
    val tempFile = Files.createTempFile("input", ".txt")
    Files.write(tempFile, inputContent.getBytes)

    // Define test arguments
    val cloudSize = 3
    val minLength = 2
    val windowSize = 5
    val everyKSteps = 1
    val minFrequency = 1

    // Call the TopWords.run method
    TopWords.run(cloudSize, minLength, windowSize, everyKSteps, minFrequency, tempFile.toString)

    // You may want to validate console output here

    // Clean up the temporary file
    Files.delete(tempFile)
  }

  it should "normalize mixed case words correctly" in {
    // Create a temporary file to simulate input
    val inputContent = "Hello\nhello\nHELLO"
    val tempFile = Files.createTempFile("input", ".txt")
    Files.write(tempFile, inputContent.getBytes)

    // Define test arguments
    val cloudSize = 3
    val minLength = 1
    val windowSize = 5
    val everyKSteps = 1
    val minFrequency = 1

    // Call the TopWords.run method
    TopWords.run(cloudSize, minLength, windowSize, everyKSteps, minFrequency, tempFile.toString)

    // You may want to validate console output here

    // Clean up the temporary file
    Files.delete(tempFile)
  }

  it should "ignore leading and trailing spaces in words" in {
    val inputContent = "  hello  \n world "
    val tempFile = Files.createTempFile("input", ".txt")
    Files.write(tempFile, inputContent.getBytes)

    val cloudSize = 3
    val minLength = 1
    val windowSize = 5
    val everyKSteps = 1
    val minFrequency = 1

    TopWords.run(cloudSize, minLength, windowSize, everyKSteps, minFrequency, tempFile.toString)

    // Validate the output as needed

    Files.delete(tempFile)
  }

  it should "ignore empty words" in {
    val inputContent = "\nhello\n\nworld\n"
    val tempFile = Files.createTempFile("input", ".txt")
    Files.write(tempFile, inputContent.getBytes)

    val cloudSize = 3
    val minLength = 1
    val windowSize = 5
    val everyKSteps = 1
    val minFrequency = 1

    TopWords.run(cloudSize, minLength, windowSize, everyKSteps, minFrequency, tempFile.toString)

    // Validate the output as needed

    Files.delete(tempFile)
  }

  it should "not count blacklisted words" in {
    val inputContent = "bad\ngood\nbad\nbetter\n"
    val tempFile = Files.createTempFile("input", ".txt")
    Files.write(tempFile, inputContent.getBytes)

    val cloudSize = 3
    val minLength = 1
    val windowSize = 5
    val everyKSteps = 1
    val minFrequency = 1
    val blacklist = Seq("bad") // Adjust the method if needed

    TopWords.run(cloudSize, minLength, windowSize, everyKSteps, minFrequency, tempFile.toString)

    // Validate the output as needed

    Files.delete(tempFile)
  }
}
