package com.alexandreesl.actor

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import com.alexandreesl.actor.KafkaExporterActor.Start
import com.alexandreesl.graph.AccountWriterGraphStage
import com.alexandreesl.graph.GraphMessages.{Account, InputMessage}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import spray.json._
import com.alexandreesl.json.JsonParsing._
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.util.{Failure, Success}

class KafkaExporterActor extends Actor with ActorLogging {

  implicit val actorSystem: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = context.system.dispatcher
  private val configConsumer = actorSystem.settings.config.getConfig("akka.kafka.consumer")
  private val consumerSettings =
    ConsumerSettings(configConsumer, new StringDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("group1")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

  override def preStart(): Unit = {
    self ! Start
  }

  override def receive: Receive = {
    case Start =>
      val done = Consumer
        .committableSource(consumerSettings, Subscriptions.topics("accounts"))
        .mapAsync(10)(msg =>
          Future.successful(InputMessage(msg.record.value.parseJson.convertTo[Account], msg.committableOffset))
        )
        .via(KafkaExporterActor.flow)
        .runWith(Sink.ignore)
      done onComplete {
        case Success(_) =>
          log.info("I completed successfully, I am so happy :)")
        case Failure(ex) =>
          log.error(ex, "I received a error! Goodbye cruel world!")
          self ! PoisonPill
      }

  }


}

object KafkaExporterActor {

  private val logger = Logger(LoggerFactory.getLogger("KafkaExporterActor"))

  def flow()(implicit actorSystem: ActorSystem, materializer: ActorMaterializer) = Flow[InputMessage].via(AccountWriterGraphStage.graph)
    .mapAsync(10) { tuple =>
      val acc = tuple._1
      logger.info(s"persisted Account: $acc")
      tuple._2.commitScaladsl()
    }

  val name = "Kafka-Exporter-actor"

  def props = Props(new KafkaExporterActor)

  case object Start

}
