package com.zcia.microservice.example.campaing.layer

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import spray.json._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import scala.concurrent.{ExecutionContextExecutor, Future}


object DataLayerActor {
  sealed trait Item
  case object Products extends Item
  case object Subscribers extends Item
  case class TopNRequest(category: String, n: Integer, best: Boolean, item: Item)
  object Error
}


class DataLayerActor extends Actor with ActorLogging with Protocol with Config {

  protected val host: String = datalayerConfig.getString("host")
  protected val port: Integer = datalayerConfig.getInt("port")

  protected val system: ActorSystem = context.system
  protected implicit val executor : ExecutionContextExecutor = context.dispatcher
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  protected val connection = Http(system).outgoingConnection(host, port)

  override def receive: Receive = {
    // Request for top n values for some aspect or concept
    case event: DataLayerActor.TopNRequest =>
      event.item match {
        // Products case
        case DataLayerActor.Products =>
          getTopNItem(event, "/products_agg/search/findByCategoryName") map { response =>
            Unmarshal(response.entity).to[String] map { payload =>
              val productsAggregations = payload.parseJson.convertTo[ProductAggregationsEmbedded].embedded.productAggs
              sender ! productsAggregations
            } recover {
              case _ => {
                log.error(s"Error while unmarshalling product aggregations ${response.entity}")
                sender ! DataLayerActor.Error
              }
            }
          } recover {
            case _ => {
              log.error(s"Error while requesting for product aggregations")
              sender ! DataLayerActor.Error
            }
          }
        // Subscribers case
        case DataLayerActor.Subscribers =>
          getTopNItem(event, "/subscribers_agg/search/findByCategoryName") map { response =>
            log.info(s"Received response: $response")
            Unmarshal(response.entity).to[String] map { payload =>
              val subscriberAggregations = payload.parseJson.convertTo[SubscriberAggregationsEmbedded].embedded.subscribersAgg
              sender ! subscriberAggregations
            } recover {
              case _ => {
                log.error(s"Error while unmarshalling subscribers aggregations ${response.entity}")
                sender ! DataLayerActor.Error
              }
            }
          } recover {
            case _ => {
              log.error(s"Error while requesting for subscribers aggregations")
              sender ! DataLayerActor.Error
            }
          }
          // Other cases aren't be supported now!
        case _ => {
          log.error(s"Not supported aspect for requesting top N: ${event.item}")
          sender ! DataLayerActor.Error
        }
      }
    // Other cases won't be supported!
    case _ => {
      log.warning("Unknown request")
      sender ! DataLayerActor.Error
    }
  }

  protected def getTopNItem(request: DataLayerActor.TopNRequest, path: String): Future[HttpResponse] = {
    val sort: String = if (request.best) "desc" else "asc"
    val queryParams: Seq[(String, String)] = Array("name" -> request.category, "page" -> "0", "size" -> s"${request.n}", "sort" -> "num", "num.dir" -> sort)
    sendToDataLayer(RequestBuilding.Get(Uri.from(path = path).withQuery(Query(queryParams: _*))))
  }

  protected def sendToDataLayer(request: HttpRequest): Future[HttpResponse] = {
    log.debug(s"Requesting following: $request")
    Source.single(request).via(connection).runWith(Sink.head)
  }

}
