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

  def getEvents(): Future[HttpResponse] = {
    val request = RequestBuilding.Get("/events")//.withHeaders(headers.`Content-Type`(ContentTypes.`application/json`))
    Source.single(request).via(client).runWith(Sink.head)
  }

  def createEvent(event: Event): Future[HttpResponse] = {
    val request = RequestBuilding.Post("/events", event)//.withHeaders(headers.`Content-Type`(ContentTypes.`application/json`))
    Source.single(request).via(client).runWith(Sink.head)
  }
}
