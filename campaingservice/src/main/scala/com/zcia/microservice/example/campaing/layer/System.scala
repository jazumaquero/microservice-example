package com.zcia.microservice.example.campaing.layer

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.ActorMaterializer

object System {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  trait LoggerExecutor extends BaseComponent {
    protected implicit val executor = system.dispatcher
    protected implicit val log = Logging(system, "app")
  }
}
