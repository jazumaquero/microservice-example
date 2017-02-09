package com.zcia.microservice.example.event.layer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future


trait DatalayerClient extends BaseService with Protocol {
  import akka.http.scaladsl.model._

  protected implicit def system: ActorSystem
  protected implicit def materializer: ActorMaterializer

  protected val client = Http(system).outgoingConnection(datalayerConfig.getString("host"), datalayerConfig.getInt("port"))

  def requestToDataLayer(request: HttpRequest) : Future[HttpResponse] = {
    Source.single(request).via(client).runWith(Sink.head)
  }
  def getEvents(): Future[HttpResponse] = {
    requestToDataLayer(RequestBuilding.Get("/events"))
  }

  def createEvent(event: BuyEvent): Future[HttpResponse] = {
    requestToDataLayer(RequestBuilding.Post("/events", event))
  }

  def createSubscriber(event: LoginEvent) : Future[HttpResponse] = {
    requestToDataLayer(RequestBuilding.Post("/subscribers",event))
  }
}
