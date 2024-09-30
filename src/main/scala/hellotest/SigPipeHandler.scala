package hellotest

import sun.misc.{Signal, SignalHandler}

object SigPipeHandler {
  def install(): Unit = {
    val signalHandler = new SignalHandler {
      override def handle(signal: Signal): Unit = {
        val signalName = signal.getName

        if (signalName eq null) {
          // signalName is null, do nothing
        } else if (signalName.equals("PIPE")) {
          println("SIGPIPE received. Exiting gracefully.")
          System.exit(0)  // Gracefully exit
        }
      }
    }

    val _ = Signal.handle(new Signal("PIPE"), signalHandler)
  }
}
