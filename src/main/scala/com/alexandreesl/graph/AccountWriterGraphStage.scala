package com.alexandreesl.graph

import akka.actor.ActorSystem
import akka.kafka.ConsumerMessage
import akka.stream.{ActorMaterializer, FlowShape}
import akka.stream.scaladsl.{Broadcast, Flow, GraphDSL, Merge, Zip}
import com.alexandreesl.model.Account

object AccountWriterGraphStage {

  case class inputMessage(acc: Account,offset: ConsumerMessage.CommittableOffset)

  def graph(implicit system: ActorSystem, materializer: ActorMaterializer)= Flow.fromGraph(GraphDSL.create() { implicit builder =>

    import GraphDSL.Implicits._

    val bcastJson = builder.add(Broadcast[inputMessage](2))
    val zip = builder.add(Zip[Account,inputMessage])

    FlowShape(bcastJson.in, zip.out)

  })

}
