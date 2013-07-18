package glokka

import java.net.URLEncoder
import akka.actor.{ActorSystem, ActorRef, Props}
import com.typesafe.config.ConfigFactory

object ActorRegistry {
  /** The sender actor will receive Option[ActorRef] */
  case class Lookup(name: String)

  /**
   * The sender actor will receive tuple (newlyCreated: Boolean, actorRef: ActorRef).
   * propsMaker is used to create the actor if it does not exist.
   */
  case class LookupOrCreate(name: String, propsMaker: () => Props)

  case class IdentifyForLookup(sed: ActorRef)
  case class IdentifyForLookupOrCreate(sed: ActorRef, propsMaker: () => Props, escapedName: String)

  //----------------------------------------------------------------------------

  val system = ActorSystem("glokka")

  val actorRef = {
    val config = ConfigFactory.load()
    if (config.getString("akka.actor.provider") == "akka.actor.LocalActorRefProvider")
      system.actorOf(Props[LocalActorRegistry], LocalActorRegistry.ACTOR_NAME)
    else
      throw new Exception("akka.actor.provider not supported")
  }

  /** Should be called at application start. */
  def start() {
    // actorRef above should have been started
  }

  def escape(name: String) = URLEncoder.encode(name, "UTF-8")
}
