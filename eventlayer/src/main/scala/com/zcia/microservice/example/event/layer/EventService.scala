package com.zcia.microservice.example.event.layer

import java.io.IOException

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes.{BadRequest, Created, OK}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.Future

trait EventService extends BaseService with DatalayerClient {

  protected val eventRoutes = buyEventRoute ~ loginEventRoute ~ aggregationsRoute

  protected def buyEventRoute = path("events" / "buy") {
    post {
      entity(as[BuyEvent]) { event =>
        complete {
          val result = createEvent(event) flatMap { r =>
            r.status match {
              case Created => Unmarshal(r.entity).to[String].map(Right(_))
              case BadRequest => Future.successful(Left(s"${r.entity}"))
              case _ => Future.failed(new IOException(s"Error connecting with datalayer!"))
            }
          }
          result.map[ToResponseMarshallable] {
            case Right(x) => Created -> x
            case Left(ex) => BadRequest -> ex
          }
        }
      }
    }
  }

  protected def loginEventRoute = path("events" / "login") {
    post {
      entity(as[LoginEvent]) { event =>
        complete {
          val result = createSubscriber(event) flatMap { r =>
            r.status match {
              case Created | BadRequest => Unmarshal(r.entity).to[String].map(Right(_))
              case _ => Future.failed(new IOException(s"Error connecting with datalayer!"))
            }
          }
          result.map[ToResponseMarshallable] {
            case Right(x) => Created -> x
          }
        }
      }
    }
  }

  protected def aggregationsRoute = path("aggregated" / """products|subscribers""".r) { resource =>
    get {
      parameters("category".as[String],"n".as[Int],"best".as[Boolean]) { (category,n,best) =>
        log.info(s"Requested aggregations for $resource with params :")
        complete {
          val result = getAggregated(s"${resource}_agg", category, n, best) flatMap { r =>
            r.status match {
              case OK => getAggregatedEntity(r,resource).map(Right(_))
              case BadRequest => Future.successful(Left(s"${r.entity}"))
              case _ => Future.failed(new IOException(s"Error connecting with datalayer!"))
            }
          }
          result.map[ToResponseMarshallable] {
            case Right(x) => OK -> x
            case Left(ex) => BadRequest -> ex
          }
        }
      }
    }
  }

}
