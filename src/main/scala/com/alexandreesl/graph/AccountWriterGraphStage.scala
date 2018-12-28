package com.alexandreesl.graph

import java.nio.file.{Paths, StandardOpenOption}

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage
import akka.stream.{ActorMaterializer, FlowShape}
import akka.stream.scaladsl.{Broadcast, FileIO, Flow, GraphDSL, Source, Zip}
import akka.util.ByteString
import com.alexandreesl.graph.GraphMessages.{Account, AccountAddressData, AccountPersonalData, InputMessage}
import spray.json._
import com.alexandreesl.json.JsonParsing._


object AccountWriterGraphStage {

  val personal = Paths.get("personal.csv")
  val address = Paths.get("address.csv")


  def graph(implicit system: ActorSystem, materializer: ActorMaterializer) = Flow.fromGraph(GraphDSL.create() { implicit builder =>

    import GraphDSL.Implicits._

    val flowPersonal = Flow[InputMessage].map(msg => {
      Source.single(AccountPersonalData(msg.acc.cod,
        msg.acc.name, msg.acc.document, msg.acc.age, msg.acc.civilStatus, msg.acc.phone, msg.acc.birthday).toJson.compactPrint + "\n")
        .map(t ⇒ ByteString(t))
        .runWith(FileIO.toPath(personal, Set(StandardOpenOption.CREATE, StandardOpenOption.APPEND)))
      msg.acc
    })

    val flowAddress = Flow[InputMessage].map(msg => {
      Source.single(AccountAddressData(msg.acc.cod,
        msg.acc.country, msg.acc.state, msg.acc.city, msg.acc.street, msg.acc.streetNum, msg.acc.neighBorhood).toJson.compactPrint + "\n")
        .map(t ⇒ ByteString(t))
        .runWith(FileIO.toPath(address, Set(StandardOpenOption.CREATE, StandardOpenOption.APPEND)))
      msg.offset
    })

    val bcastJson = builder.add(Broadcast[InputMessage](2))
    val zip = builder.add(Zip[Account, ConsumerMessage.CommittableOffset])

    bcastJson ~> flowPersonal ~> zip.in0
    bcastJson ~> flowAddress ~> zip.in1


    FlowShape(bcastJson.in, zip.out)

  })

}
