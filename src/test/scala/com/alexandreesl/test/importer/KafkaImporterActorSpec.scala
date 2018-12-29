package com.alexandreesl.test.importer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.testkit.{TestKit, TestProbe}
import com.alexandreesl.actor.KafkaImporterActor
import com.alexandreesl.graph.GraphMessages.Account
import com.alexandreesl.test.TestEnvironment

import scala.concurrent.duration._

class KafkaImporterActorSpec extends TestKit(ActorSystem("MyTestSystem")) with TestEnvironment {
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  override def afterAll() = TestKit.shutdownActorSystem(system)

  "A csv line in a file " when {
    val csv = "1,\"alexandre eleuterio santos lourenco 1\",\"43456754356\",36,\"Single\",\"+5511234433443\",\"21/06/1982\",\"Brazil\",\"São Paulo\",\"São Paulo\",\"false st\",3134,\"neigh 1\""

    " should convert to Account case class " in {

      val probe = TestProbe()

      Source.single(csv)
        .via(KafkaImporterActor.flow)
        .to(Sink.actorRef(probe.ref, "completed"))
        .run()

      probe.expectMsg(2.seconds, Account(1, "alexandre eleuterio santos lourenco 1",
        "43456754356", 36, "Single", "+5511234433443", "21/06/1982",
        "Brazil", "São Paulo", "São Paulo", "false st", 3134, "neigh 1"))
      probe.expectMsg(2.seconds, "completed")

    }

  }

}
