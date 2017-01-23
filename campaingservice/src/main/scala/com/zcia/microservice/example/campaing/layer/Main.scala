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

object Main extends App with Config with System.LoggerExecutor with CampaingService {
  override protected implicit def materializer = System.materializer
  override protected implicit def system = System.system
  override protected implicit val executor = System.system.dispatcher
}