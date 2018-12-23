import akka.actor.ActorSystem

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("akka-streams-lab")
}