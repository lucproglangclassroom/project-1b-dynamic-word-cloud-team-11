package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import org.scalactic.Prettifier.default

class TestMain extends AnyFlatSpec {

  def captureOutput(f: => Unit): String = {
  val stream = new ByteArrayOutputStream()
  Console.withOut(new PrintStream(stream))(f)  // Redirect console output to stream
  Option(stream.toString).getOrElse("") // Provide a default empty string
}

  "Main" should "correctly handle command-line arguments" in {
    val cloudSize = 5
    val minLength = 4
    val windowSize = 5
    val everyKSteps = 2
    val minFrequency = 2

    val result = captureOutput {
    Main.run(cloudSize, minLength, windowSize, everyKSteps, minFrequency, "", "")
    }

    // Verify that arguments were parsed correctly and processed
    result must include ("Cloud Size: 5")
    result must include ("Minimum Length: 4")
  }
}
