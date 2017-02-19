package com.zcia.microservice.example.campaing.layer

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import spray.json._

import scala.concurrent.{ExecutionContextExecutor, Future}


object DataLayerActor {
  sealed trait Item
  case object Products extends Item
  case object Subscribers extends Item
  case class TopNRequest(category: String, n: Integer, best: Boolean, item: Item)
  case object Error
}

class DataLayerActor extends Actor with ActorLogging with DataLayerProtocol with DataLayerConfig {

  import  DataLayerActor._

  protected val system: ActorSystem = context.system
  protected implicit val executor : ExecutionContextExecutor = context.dispatcher
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  protected val connection = Http(system).outgoingConnection(host, port)

  override def receive: Receive = {
    // Request for top n values for some aspect or concept
    case event: TopNRequest =>
      event.item match {
        case Products => topProducts(sender, event.category, event.n, event.best)
        case Subscribers => topSubscribers(sender, event.category, event.n, event.best)
    }
    // Other cases won't be supported!
    case _ =>
      log.warning("Unknown request")
      sender ! Error
  }

  private def getSort(isBest: Boolean) : String = if(isBest) "desc" else "asc"

  private def getQueryParams(category: String, n: Integer, best: Boolean)= Array("name" -> category, "page" -> "0", "size" -> s"$n", "sort" -> "num", "num.dir" -> getSort(best))

  protected def topProducts(requestor: ActorRef, category: String, n: Integer, best: Boolean) : Unit = {
    val path = "/products_agg/search/findByCategoryName"
    val params = getQueryParams(category,n,best)
    sendToDataLayer(RequestBuilding.Get(Uri.from(path = path).withQuery(Query(params: _*)))) map { response =>
      log.info(s"Response for request top products :$response")
      // FIXME: implement support to HAL+JSON content-type to avoid this workaround
      Unmarshal(response.entity).to[String] map { payload =>
        requestor ! payload.parseJson.convertTo[ProductAggregationsEmbedded]
      } recover {
        case _ =>
          log.error(s"Error while unmarshalling product aggregations ${response.entity}")
          requestor ! Error
      }
    } recover {
      case _ =>
        log.error(s"Error while requesting for product aggregations")
        requestor ! Error
    }
  }

  protected def topSubscribers(requestor: ActorRef, category: String, n: Integer, best: Boolean) : Unit = {
    val path = "/subscribers_agg/search/findByCategoryName"
    val params = getQueryParams(category,n,best)
    sendToDataLayer(RequestBuilding.Get(Uri.from(path = path).withQuery(Query(params: _*)))) map { response =>
      log.info(s"Response for request top subscribers :$response")
      // FIXME: implement support to HAL+JSON content-type to avoid this workaround
      Unmarshal(response.entity).to[String] map { payload =>
        requestor ! payload.parseJson.convertTo[SubscriberAggregationsEmbedded]
      } recover {
        case _ =>
          log.error(s"Error while unmarshalling subscribers aggregations ${response.entity}")
          requestor ! Error
      }
    } recover {
      case _ =>
        log.error(s"Error while requesting for subscribers aggregations")
        requestor ! Error
    }
  }

  protected def sendToDataLayer(request: HttpRequest): Future[HttpResponse] = {
    log.debug(s"Requesting following: $request")
    Source.single(request).via(connection).runWith(Sink.head)
  }

}
