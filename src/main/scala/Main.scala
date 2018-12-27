import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.alpakka.csv.scaladsl.{CsvParsing, CsvToMap}
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import akka.util.ByteString
import com.alexandreesl.model.Account
import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory

object Main extends App {
  implicit val system: ActorSystem = ActorSystem("akka-streams-lab")
  implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(system))
  implicit val ec = system.dispatcher
  private val logger = Logger(LoggerFactory.getLogger("Main"))
  implicit def optionByteStringToString(opt: Option[ByteString]): String = opt.get.decodeString(StandardCharsets.UTF_8)
  implicit def optionByteStringToLong(opt: Option[ByteString]): Long = opt.get.decodeString(StandardCharsets.UTF_8).toLong
  implicit def optionByteStringToInt(opt: Option[ByteString]): Int = opt.get.decodeString(StandardCharsets.UTF_8).toInt

  logger.info("Starting streams...")

  private def convertToClass(csv: Map[String, ByteString]): Account = Account(csv.get("COD"),
    csv.get("NAME"), csv.get("DOCUMENT"),
    csv.get("AGE"), csv.get("CIVIL_STATUS"),
    csv.get("PHONE"), csv.get("BIRTHDAY"),
    csv.get("HOME_COUNTRY"), csv.get("HOME_STATE"),
    csv.get("HOME_CITY"), csv.get("HOME_STREET"),
    csv.get("HOME_STREETNUM"), csv.get("HOME_NEIGHBORHOOD"))

  private val flow = Flow[String].filter(s => !s.startsWith("COD;")).map(ByteString(_))
    .via(CsvParsing.lineScanner(CsvParsing.SemiColon, CsvParsing.DoubleQuote, CsvParsing.DoubleQuote))
    .via(CsvToMap.withHeaders("COD",
      "NAME",
      "DOCUMENT",
      "AGE",
      "CIVIL_STATUS",
      "PHONE",
      "BIRTHDAY",
      "HOME_COUNTRY",
      "HOME_STATE",
      "HOME_CITY",
      "HOME_STREET",
      "HOME_STREETNUM",
      "HOME_NEIGHBORHOOD"))
    .map(map =>
      convertToClass(map))

  FileIO.fromPath(Paths.get("input1.csv"))
    .via(Framing.delimiter(ByteString("\n"), 4096)
      .map(_.decodeString(StandardCharsets.UTF_8)))
    .via(flow)
    .runWith(Sink.foreach(account =>
      logger.info(s"account: $account")))

  logger.info("Stream system is initialized!")

}