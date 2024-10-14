package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Try
import java.io._
import scala.Console // Import Scala's Console

class TestSigPipeHandler extends AnyFlatSpec {

  "TopWords" should "terminate gracefully when SIGPIPE is triggered" in {
    // Set up a piped output stream to simulate output redirection
    val outputStream = new PipedOutputStream()
    val inputStream = new PipedInputStream(outputStream)

    // Run the program in a separate thread and pipe its output
    val future = Future {
      scala.Console.withOut(outputStream) {  // Ensure this uses Scala's Console
        TopWords.main(Array("--cloud-size", "3", "--min-length", "2", "--window-size", "5"))
      }
    }

    // Simulate SIGPIPE by closing the input stream after a short delay
    Thread.sleep(500) // Give the process a moment to start
    inputStream.close()

    // Wait for the program to terminate and capture its behavior
    val result = Try(Await.result(future, scala.concurrent.duration.Duration.Inf))

    // Verify that the program terminated gracefully without crashing
    assert(result.isFailure == false, "Program did not terminate gracefully on SIGPIPE")
  }
}
