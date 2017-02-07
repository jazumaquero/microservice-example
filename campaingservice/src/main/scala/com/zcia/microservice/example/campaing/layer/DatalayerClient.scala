package com.zcia.microservice.example.campaing.layer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

trait DatalayerClient extends BaseService with Protocol {
  protected implicit def system: ActorSystem

  protected implicit def materializer: ActorMaterializer

  protected val datalayerClient = Http(system).outgoingConnection(datalayerConfig.getString("host"), datalayerConfig.getInt("port"))

  protected def sendToDataLayer(request: HttpRequest): Future[HttpResponse] = {
    log.debug(s"Requesting following: $request")
    Source.single(request).via(datalayerClient).runWith(Sink.head)
  }

  protected def getTopNItem(category: String, n: Integer, best: Boolean, path: String): Future[HttpResponse] = {
    val sort: String = if (best) "desc" else "asc"
    val queryParams: Seq[(String, String)] = Array("name" -> category, "page" -> "1", "size" -> s"$n", "sort" -> "num", "num.dir" -> sort)
    sendToDataLayer(RequestBuilding.Get(Uri.from(path = path).withQuery(Query(queryParams: _*))))
  }
  protected def getTopNSubscribers(category: String, n: Integer, best: Boolean): Future[HttpResponse] = {
    getTopNItem(category, n, best, "/subscribers_agg/search/findByCategoryName")
  }

  protected def getTopNProducts(category: String, n: Integer, best: Boolean): Future[HttpResponse] = {
    getTopNItem(category, n, best, "/products_agg/search/findByCategoryName")
  }
}
