package com.alexandreesl.graph

import akka.kafka.ConsumerMessage

object GraphMessages {

  case class Account(cod: Long, name: String, document: String, age: Int, civilStatus: String,
                     phone: String, birthday: String, country: String, state: String,
                     city: String, street: String, streetNum: Long, neighBorhood: String)

  case class InputMessage(acc: Account, offset: ConsumerMessage.CommittableOffset)

  case class AccountPersonalData(cod: Long, name: String, document: String, age: Int, civilStatus: String,
                                 phone: String, birthday: String)

  case class AccountAddressData(cod: Long, country: String, state: String,
                                city: String, street: String, streetNum: Long, neighBorhood: String)

}
