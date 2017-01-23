package com.zcia.microservice.example.event.layer

import spray.json.DefaultJsonProtocol

/**
  * Created by zuma on 21/01/17.
  */
case class Event(productId: Long, subscriberId: Long)

trait Protocol extends DefaultJsonProtocol {
  implicit val eventFormatter = jsonFormat2(Event)
}