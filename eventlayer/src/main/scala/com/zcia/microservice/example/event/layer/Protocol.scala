package com.zcia.microservice.example.event.layer

import spray.json.DefaultJsonProtocol

case class Event(productId: Long, subscriberId: Long)

trait Protocol extends DefaultJsonProtocol {
  implicit val eventFormatter = jsonFormat2(Event)
}