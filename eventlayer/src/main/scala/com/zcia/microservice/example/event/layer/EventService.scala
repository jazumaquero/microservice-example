package com.zcia.microservice.example.event.layer

import java.io.IOException

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes.{BadRequest, OK, Created}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.Future

trait EventService extends BaseService with DatalayerClient {

  protected def postEvent = path("events") {
    post {
      entity(as[Event]) { event =>
        complete {
          val result = createEvent(event) flatMap { r =>
            r.status match {
              case Created =>Unmarshal(r.entity).to[String].map(Right(_))
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

  protected val eventRoutes = postEvent
}
