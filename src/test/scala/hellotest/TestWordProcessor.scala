package hellotest

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers._

class TestWordProcessor extends AnyFlatSpec {

  "Word Processor" should "process words correctly" in {
    val words = Seq("hello", "world", "hello", "scala")
    val result = WordProcessor.processWords(words, 2, 3, 3, 1)
    result must equal(Map("hello" -> 2, "world" -> 1, "scala" -> 1))
  }

  it should "respect the minimum frequency" in {
    val words = Seq("hello", "world", "hello", "scala", "scala", "hello")
    val result = WordProcessor.processWords(words, 2, 3, 3, 2)
    result must equal(Map("hello" -> 3, "scala" -> 2))
  }
}
