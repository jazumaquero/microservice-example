package com.zcia.microservice.example.event.layer

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.OutgoingConnection
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Directives
import akka.stream.scaladsl.Flow

import scala.concurrent.Future


/**
  * Created by zuma on 21/01/17.
  */
object System {
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  trait LoggerExecutor extends BaseComponent {
    protected implicit val executor = system.dispatcher
    protected implicit val log = Logging(system, "app")
  }
}

object Main extends App with Config with System.LoggerExecutor with EventService {
  import System._
  import Directives._

  val host: String = datalayerConfig.getString("host")
  val port: Int = datalayerConfig.getInt("port")
  override protected implicit val datalayerUri = s"http://${host}:${port}"
  override protected implicit val client = Http(system).outgoingConnection(host, port)

  Http(System.system).bindAndHandle(handler=eventRoutes, interface=httpConfig.getString("interface"), port=httpConfig.getInt("port"))


}