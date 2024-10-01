package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._

class TestMain extends AnyFlatSpec {

  "TopWords" should "run correctly with given arguments" in {
    // Define test arguments
    val cloudSize = 3
    val minLength = 2
    val windowSize = 5
    val everyKSteps = 2
    val minFrequency = 1

    // Call the TopWords.run method
    TopWords.run(cloudSize, minLength, windowSize, everyKSteps, minFrequency)

    // You may want to capture and validate the console output here, similar to the captureOutput method
  }
}
