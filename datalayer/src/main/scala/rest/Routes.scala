package rest

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Route}
import dao.{BaseEntity, BaseRepository, BaseTable}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

import scala.util.{Failure, Success}

abstract class BaseProtocol[E<:BaseEntity] extends DefaultJsonProtocol {

abstract class BaseCrudRoutest [T <: BaseTable[Entity], Entity: BaseEntity, Dto]
(basePath: String, repository: BaseRepository[T, Entity])
  extends Directive with BaseProtocol[Entity]{

  protected val getEntityRoute = path(basePath / LongNumber) {
    (id) =>
      get {
        validate(id > 0, "") {
          onComplete(repository.findById(id)) {
            case Success(entityOpt) => entityOpt match {
              case Some(entity) => complete(entity)
              case None => complete(NotFound, s"Missing entity with id = $id")
            }
            case Failure(ex) => complete(InternalServerError, s"Some error ocurred: ${ex.getMessage}")
          }
        }
      }
  }

  protected val getAllEntitiesRoute = path(basePath) {
    get {
      onComplete(repository.findAll) {
        case Success(entities) => complete(entities)
        case Failure(ex) => complete(InternalServerError, s"Some error ocurred: ${ex.getMessage}")
      }
    }
  }

  protected val postEntityRoute = path(basePath) {
    post {
      entity(as[Entity]) {
        e =>
          onComplete[Entity](repository.save(e)) {
            case Success(_) => complete(Created)
            case Failure(ex) => complete(InternalServerError, s"Some error ocurred: ${ex.getMessage}")
          }
      }
    }
  }

  protected val putEntityRoute = path(basePath / LongNumber) {
    (id) =>
      put {
        validate(id > 0, "") {
          entity(as[Entity]) {
            e =>
              onComplete(repository.updateById(id, e)) {
                case Success(_) => complete(StatusCodes.Accepted)
                case Failure(ex) => complete(InternalServerError, s"Some error ocurred: ${ex.getMessage}")
              }
          }
        }
      }
  }

  protected val deleteEntityRoute = path(basePath / LongNumber) {
    (id) =>
      delete {
        validate(id > 0, "") {
          onComplete(repository.deleteById(id)) {
            case Success(_) => complete(StatusCodes.Accepted)
            case Failure(ex) => complete(InternalServerError, s"Some error ocurred: ${ex.getMessage}")
          }
        }
      }
  }

  protected val routes: Route = getEntityRoute ~ getAllEntitiesRoute ~ postEntityRoute ~ putEntityRoute ~ deleteEntityRoute
}