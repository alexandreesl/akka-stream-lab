
import akka.actor.{ActorSystem, OneForOneStrategy, Props, SupervisorStrategy}
import akka.pattern.{Backoff, BackoffSupervisor}
import com.alexandreesl.actor.{KafkaExporterActor, KafkaImporterActor}
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.duration._

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("akka-streams-lab")
  private val logger = Logger(LoggerFactory.getLogger("Main"))

  logger.info("Starting streams...")

  private val supervisorStrategy = OneForOneStrategy() {
    case ex: Exception =>
      logger.info(s"exception: $ex")
      SupervisorStrategy.Restart

  }
  private val importerProps: Props = BackoffSupervisor.props(
    Backoff.onStop(
      childProps = KafkaImporterActor.props,
      childName = KafkaImporterActor.name,
      minBackoff = 3.seconds,
      maxBackoff = 30.seconds,
      randomFactor = 0.2
    ).withSupervisorStrategy(supervisorStrategy)
  )
  private val exporterProps: Props = BackoffSupervisor.props(
    Backoff.onStop(
      childProps = KafkaExporterActor.props,
      childName = KafkaExporterActor.name,
      minBackoff = 3.seconds,
      maxBackoff = 30.seconds,
      randomFactor = 0.2
    ).withSupervisorStrategy(supervisorStrategy)
  )


  system.actorOf(importerProps, "Kafka-importer")
  system.actorOf(exporterProps, "Kafka-exporter")

  logger.info("Stream system is initialized!")

}