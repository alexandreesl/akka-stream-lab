
import akka.actor.ActorSystem
import com.alexandreesl.actor.{KafkaExporterActor, KafkaImporterActor}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory


object Main extends App {

  implicit val system: ActorSystem = ActorSystem("akka-streams-lab")
  private val logger = Logger(LoggerFactory.getLogger("Main"))

  logger.info("Starting streams...")

  system.actorOf(KafkaImporterActor.props, KafkaImporterActor.name)
  system.actorOf(KafkaExporterActor.props, KafkaExporterActor.name)

  logger.info("Stream system is initialized!")

}