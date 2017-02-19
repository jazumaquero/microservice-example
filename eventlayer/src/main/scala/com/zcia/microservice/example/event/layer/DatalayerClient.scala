package com.zcia.microservice.example.event.layer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import spray.json._

import scala.concurrent.Future


trait DatalayerClient extends BaseService with Protocol {
  import akka.http.scaladsl.model._
  import DefaultJsonProtocol._

  protected implicit def system: ActorSystem
  protected implicit def materializer: ActorMaterializer

  protected val client = Http(system).outgoingConnection(datalayerConfig.getString("host"), datalayerConfig.getInt("port"))

  def requestToDataLayer(request: HttpRequest) : Future[HttpResponse] = {
    Source.single(request).via(client).runWith(Sink.head)
  }

  def createEvent(event: BuyEvent): Future[HttpResponse] = {
    requestToDataLayer(RequestBuilding.Post("/events", event))
  }

  def createSubscriber(event: LoginEvent) : Future[HttpResponse] = {
    requestToDataLayer(RequestBuilding.Post("/subscribers",event))
  }

  def getAggregated(resource: String, category: String, n: Int, best: Boolean) : Future[HttpResponse] = {
    requestToDataLayer(RequestBuilding.Get(Uri.from(path = getPath(resource)).withQuery(Query(getQueryParams(category,n,best): _*))))
  }

  protected def getProductsAggregated(response: HttpResponse) : Future[Seq[ProductAggregation]] = {
    Unmarshal(response.entity).to[String] map { payload =>
      payload.parseJson.convertTo[ProductAggregationsEmbedded].embedded.productAggs
    }
  }

  protected def getSubscribedAggregated(response: HttpResponse) : Future[Seq[SubscriberAggregation]] = {
    Unmarshal(response.entity).to[String] map { payload =>
      payload.parseJson.convertTo[SubscriberAggregationsEmbedded].embedded.subscribersAgg
    }
  }

  protected def getAggregatedEntity(response: HttpResponse, resource: String) : Future[JsValue] = resource match {
    case "products" => getProductsAggregated(response).map(_.toJson)
    case "subscribers" => getSubscribedAggregated(response).map(_.toJson)
  }

  private def getPath(resource: String) : String = s"/$resource/search/findByCategoryName/"

  private def getSort(isBest: Boolean) : String = if(isBest) "desc" else "asc"

  private def getQueryParams(category: String, n: Integer, best: Boolean)= Array("name" -> category, "page" -> "0", "size" -> s"$n", "sort" -> "num", "num.dir" -> getSort(best))
}
