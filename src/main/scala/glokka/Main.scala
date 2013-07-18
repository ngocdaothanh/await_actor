package glokka

object Main {
  def main(args: Array[String]) {
    import akka.actor.{Actor, Props}
    import glokka.ActorRegistry

    ActorRegistry.start()

    val r = ActorRegistry.system.actorOf(Props(new Actor {
      def receive = {
        case "lookup" =>
          ActorRegistry.actorRef ! ActorRegistry.Lookup("nonexistent")

        case x =>
          println(x)
          context.stop(self)
      }
    }))
    r ! "lookup"
  }
}
