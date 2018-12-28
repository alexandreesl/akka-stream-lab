package com.alexandreesl.json

import com.alexandreesl.graph.GraphMessages.{Account, AccountAddressData, AccountPersonalData}
import spray.json.DefaultJsonProtocol


object JsonParsing extends DefaultJsonProtocol {

  implicit val accountFormat = jsonFormat13(Account)
  implicit val accountPersonalFormat = jsonFormat7(AccountPersonalData)
  implicit val accountAddressFormat = jsonFormat7(AccountAddressData)

}
