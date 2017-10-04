package controllers

import play.api.mvc._
import play.api.libs.streams.ActorFlow
import javax.inject.Inject
import akka.actor.ActorSystem
import akka.stream.Materializer

import akka.actor._

import Utils._
import Engine._

case class EngineMessage(
  action: String = "",
  name: String = "",
  command: String = "",
  buffer: String = "",
  available: List[String] = List[String]()
) {

}

object EngineSocketActor {
  def props(out: ActorRef) = Props(new EngineSocketActor(out))
}

class EngineSocketActor(out: ActorRef) extends Actor {
  def send(em: EngineMessage) {
    out ! upickle.default.write[EngineMessage](em)
  }
  def receive = {
    case msg: String => {
      try {
        val em = upickle.default.read[EngineMessage](msg)

        println("em : " + em)

        loadrecords

        em.action match {
          case "sendavailable" => send(EngineMessage(action = "available", available = enginerecords.available))
          case "start" => {
            val er = enginerecords.get(em.name)
            unload
            e = Engine(er.path)
            println("setting out to " + out)
            setout(out)
            load
            val confignormalized = er.config.replaceAll("\\r", "")
            issuecommand(confignormalized)
          }
          case "issue" => {
            println("setting out to " + out)
            setout(out)
            issuecommand(em.command)
          }
        }
      } catch { case e: Throwable => }
    }
  }
}

class SocketController @Inject() (cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {

  def ws = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef { out =>
      EngineSocketActor.props(out)
    }
  }
}