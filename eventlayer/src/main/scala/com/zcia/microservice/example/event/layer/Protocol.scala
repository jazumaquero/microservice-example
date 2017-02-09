package com.zcia.microservice.example.event.layer

import spray.json.DefaultJsonProtocol

case class BuyEvent(productId: Long, subscriberId: Long)
case class LoginEvent(name:String, email:String)

trait Protocol extends DefaultJsonProtocol {
  implicit val buyEventFormatter = jsonFormat2(BuyEvent)
  implicit val loginEventFormatter = jsonFormat2(LoginEvent)
}