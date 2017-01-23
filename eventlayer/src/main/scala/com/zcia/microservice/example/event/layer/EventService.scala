package com.zcia.microservice.example.event.layer

import akka.http.scaladsl.Http.OutgoingConnection
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse}
import akka.http.scaladsl.model.StatusCodes.OK
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.concurrent.Future

/**
  * Created by zuma on 21/01/17.
  */
trait DatalayerClient{
  protected implicit def system : ActorSystem
  protected implicit def materializer : ActorMaterializer
  protected implicit def datalayerClient : Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]]
  protected implicit def datalayerUri : String

  def endpoint(request: HttpRequest): Future[HttpResponse] = Source.single(request).via(datalayerClient).runWith(Sink.head)

  def request(): HttpRequest = HttpRequest(method=HttpMethods.GET, uri=uri)
}

trait EventService extends BaseService {
  protected implicit val datalayerUri: String
  protected implicit val client : Flow[HttpRequest, HttpResponse, Future[OutgoingConnection]]

  protected val eventRoutes = path("events") {
    get {
      complete(OK, "Oh year")
    }
  }
}
