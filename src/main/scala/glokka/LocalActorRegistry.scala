package glokka

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

import akka.actor.{Actor, ActorRef, Props, Identify, ActorIdentity, ActorLogging}
import akka.pattern.ask
import akka.util.Timeout

object LocalActorRegistry {
  val ACTOR_NAME = ActorRegistry.escape(getClass.getName)
}

class LocalActorRegistry extends Actor with ActorLogging {
  import ActorRegistry._
  import LocalActorRegistry._

  override def preStart() {
    log.info("ActorRegistry starting: " + self)
  }

  def receive = {
    case Lookup(name) =>
      implicit val out = Timeout(5.seconds)

      val sel = context.actorSelection(escape(name))
      val fut = sel.ask(Identify(None)).mapTo[ActorIdentity].map(_.ref)
      val opt = Await.result(fut, out.duration)
      sender ! opt
  }

  /*
  def receive = {
    case Lookup(name) =>
      val sel = context.actorSelection(escape(name))
      val sed = sender
      sel ! Identify(IdentifyForLookup(sed))

    case LookupOrCreate(name, propsMaker) =>
      val esc = escape(name)
      val sel = context.actorSelection(esc)
      val sed = sender
      sel ! Identify(IdentifyForLookupOrCreate(sed, propsMaker, esc))

    //--------------------------------------------------------------------------

    case ActorIdentity(IdentifyForLookup(sed), opt) =>
      sed ! opt

    case ActorIdentity(IdentifyForLookupOrCreate(sed, _, _), Some(actorRef)) =>
      sed ! (false, actorRef)

    case ActorIdentity(IdentifyForLookupOrCreate(sed, propsMaker, escapedName), None) =>
      val actorRef = context.actorOf(propsMaker(), escapedName)
      sed ! (true, actorRef)
  }
  */
}
