package com.alexandreesl.test.exporter

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage.CommittableOffset
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.{TestKit, TestProbe}
import com.alexandreesl.actor.KafkaExporterActor
import com.alexandreesl.graph.GraphMessages.{Account, InputMessage}
import com.alexandreesl.test.TestEnvironment
import org.mockito.Mockito.{times, verify, when}
import org.scalatest.mockito.MockitoSugar.mock

import scala.concurrent.Future
import scala.concurrent.duration._

class KafkaExporterActorSpec extends TestKit(ActorSystem("MyTestSystem")) with TestEnvironment {
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  override def afterAll() = TestKit.shutdownActorSystem(system)

  "A Account object " when {

    val offset = mock[CommittableOffset]
    when(offset commitScaladsl) thenReturn Future.successful(Done)
    val account = Account(1, "alexandre eleuterio santos lourenco 1",
      "43456754356", 36, "Single", "+5511234433443", "21/06/1982",
      "Brazil", "São Paulo", "São Paulo", "false st", 3134, "neigh 1")
    val inputMessage = InputMessage(account, offset)

    " should pass through the graph stage " in {

      val probe = TestProbe()
      Source.single(inputMessage)
        .via(KafkaExporterActor.flow)
        .to(Sink.actorRef(probe.ref, "completed"))
        .run()

      probe.expectMsg(2.seconds, Done)
      probe.expectMsg(2.seconds, "completed")
      verify(offset, times(1)) commitScaladsl

    }

  }

}
