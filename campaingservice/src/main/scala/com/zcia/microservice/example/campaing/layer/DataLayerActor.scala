package com.zcia.microservice.example.campaing.layer

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
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
        case Products => getAggregated(sender, "products", event.category, event.n, event.best)
        case Subscribers => getAggregated(sender, "subscribers", event.category, event.n, event.best)
    }
    // Other cases won't be supported!
    case _ =>
      log.warning("Unknown request")
      sender ! Error
  }

  private def getAggregated(requestor: ActorRef, resource: String, category: String, n: Integer, best: Boolean) : Unit = {
    val params = Array("category" -> category, "n" -> n.toString, "best" -> best.toString)
    val path = s"/aggregated/$resource"
    sendToDataLayer(RequestBuilding.Get(Uri.from(path = path).withQuery(Query(params:_*)))) map { response =>
      log.debug(s"Response for request top $resource :$response")
      resource match {
        case "products" =>
          Unmarshal[HttpEntity](response.entity.withContentType(ContentTypes.`application/json`)).to[Seq[ProductAggregation]] map { products =>
            requestor ! ProductAggregations(products)
          }
        case "subscribers" =>
          Unmarshal[HttpEntity](response.entity.withContentType(ContentTypes.`application/json`)).to[Seq[SubscriberAggregation]] map { subscribers =>
            requestor ! SubscriberAggregations(subscribers)
          }
        case _ =>
          log.error(s"Error while unmarshalling $resource aggregations ${response.entity}")
          requestor ! Error
      }
    } recover {
      case _ =>
        log.error(s"Error while requesting for $resource aggregations")
        requestor ! Error
    }
  }

  protected def sendToDataLayer(request: HttpRequest): Future[HttpResponse] = {
    log.debug(s"Requesting following: $request")
    Source.single(request).via(connection).runWith(Sink.head)
  }

}
