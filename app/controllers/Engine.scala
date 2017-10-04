package controllers

import collection.JavaConverters._

import akka.actor._

object Engine {

  var e = Engine()

  def load {
    e.Load
  }

  def unload {
    e.Unload
  }

  def issuecommand(command: String) {
    e.IssueCommand(command)
  }

  def setout(out: ActorRef) {
    e.SetOut(out)
  }

}

case class Engine(
  path: String = "",
  uniqueid: Int = 0
) {
  var out: ActorRef = null

  var engineprocess: Process = null
  var enginein: java.io.InputStream = null
  var engineout: java.io.OutputStream = null
  var enginereadthread: Thread = null

  def SetOut(setout: ActorRef) {
    out = setout
  }

  def ProcessEngineOut(buffer: String) {
    //println(buffer)
    out ! upickle.default.write[EngineMessage](EngineMessage(action = "thinkingoutput", buffer = buffer))
  }

  def CreateEngineReadThread: Thread =
    {
      val truethis = this
      new Thread(new Runnable {
        def run {
          var buffer = ""
          while (!Thread.currentThread().isInterrupted()) {
            {
              try {
                val chunkobj = enginein.read()
                try {
                  val chunk = chunkobj.toChar
                  if (chunk == '\n') {
                    ProcessEngineOut(buffer)
                    buffer = ""
                  } else {
                    buffer += chunk
                  }
                } catch {
                  case e: Throwable =>
                    {
                      val emsg = s"engine read not a char exception, chunk: $chunkobj, id: $uniqueid, path: $path"
                      println(emsg)
                    }
                }
              } catch {
                case e: Throwable =>
                  {
                    val emsg = s"engine read IO exception, id: $uniqueid, path: $path"
                    println(emsg)
                  }
              }
            }
          }
        }
      })
    }

  def IssueCommand(command: String) {
    if (command == null) return

    val fullcommand = command + "\n"

    try {
      engineout.write(fullcommand.getBytes())
      engineout.flush()
    } catch {
      case e: Throwable =>
        {
          val emsg = s"engine write IO exception, command: $command, id: $uniqueid, path: $path"
          println(emsg)
          e.printStackTrace
        }
    }
  }

  def Unload {

    if (enginereadthread != null) {
      enginereadthread.interrupt()
      enginereadthread = null
    }

    if (engineprocess != null) {
      engineprocess.destroy()
      engineprocess = null
    }

    enginein = null
    engineout = null

  }

  def Load: String = {

    if (engineprocess != null) return "already running"

    val progandargs: List[String] = List(path)
    val processbuilder = new ProcessBuilder(progandargs.asJava)
    val epf = new java.io.File(path)

    if (epf.exists()) {
      processbuilder.directory(new java.io.File(epf.getParent()))
    } else {
      return (s"engine $path failed, directory does not exist")
    }

    try {
      engineprocess = processbuilder.start()
    } catch {
      case e: Throwable =>
        {
          return (s"engine $path failed, process could no be created")
        }
    }

    enginein = engineprocess.getInputStream()
    engineout = engineprocess.getOutputStream()
    enginereadthread = CreateEngineReadThread
    enginereadthread.start()

    "ok"

  }

}