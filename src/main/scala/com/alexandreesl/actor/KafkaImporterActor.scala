package com.alexandreesl.actor

import java.nio.file.Paths

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, Framing}
import akka.util.ByteString
import com.alexandreesl.actor.KafkaImporterActor.Start
import com.alexandreesl.graph.GraphMessages.Account
import com.alexandreesl.json.JsonParsing._
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.StringSerializer
import spray.json._

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}


class KafkaImporterActor extends Actor with ActorLogging {

  implicit val actorSystem: ActorSystem = context.system
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val dispatcher: ExecutionContextExecutor = context.system.dispatcher


  private val configProducer = actorSystem.settings.config.getConfig("akka.kafka.producer")
  private val producerSettings =
    ProducerSettings(configProducer, new StringSerializer, new StringSerializer)
      .withBootstrapServers("localhost:9092")


  override def preStart(): Unit = {
    self ! Start
  }

  override def receive: Receive = {
    case Start =>
      val done = FileIO.fromPath(Paths.get("input1.csv"))
        .via(Framing.delimiter(ByteString("\n"), 4096)
          .map(_.utf8String))
        .via(KafkaImporterActor.flow)
        .map(value => new ProducerRecord[String, String]("accounts", value.toJson.compactPrint))
        .runWith(Producer.plainSink(producerSettings))
      done onComplete {
        case Success(_) =>
          log.info("I completed successfully, I am so happy :)")
        case Failure(ex) =>
          log.error(ex, "I received a error! Goodbye cruel world!")
          self ! PoisonPill
      }

  }

}

object KafkaImporterActor {

  private def convertToClass(csv: Array[String]): Account = {
    Account(csv(0).toLong,
      csv(1), csv(2),
      csv(3).toInt, csv(4),
      csv(5), csv(6),
      csv(7), csv(8),
      csv(9), csv(10),
      csv(11).toLong, csv(12))
  }

  val flow = Flow[String].filter(s => !s.contains("COD"))
    .map(line => {
      convertToClass(line.split(","))
    })

  val name = "Kafka-Importer-actor"

  def props = Props(new KafkaImporterActor)

  case object Start

}
