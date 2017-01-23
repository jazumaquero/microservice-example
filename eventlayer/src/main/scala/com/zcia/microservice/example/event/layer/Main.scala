package com.zcia.microservice.example.event.layer

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives


object System {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  trait LoggerExecutor extends BaseComponent {
    protected implicit val executor = system.dispatcher
    protected implicit val log = Logging(system, "app")
  }
}

object Main extends App with Config with System.LoggerExecutor with EventService {
  import Directives._

  override protected implicit def materializer = System.materializer
  override protected implicit def system = System.system
  override protected implicit val executor = System.system.dispatcher

  Http(System.system).bindAndHandle(handler=eventRoutes, interface=httpConfig.getString("interface"), port=httpConfig.getInt("port"))
}