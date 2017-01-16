package rest


import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import dao.{BaseEntity, BaseRepository, BaseTable}

import scala.util.{Failure, Success}

abstract class BaseCrudService[E <: BaseEntity, T <: BaseTable[E]]() {
  val basePath: String
  val repository: BaseRepository[T, E]

  protected val createRoute = path(basePath) {
    post {
      entity(as[E]) {
        (e) => {
          onComplete(repository.save(e)) {
            case Success(_) => complete(Created)
            case Failure(ex) => complete(InternalServerError, s"Some error happen while creating entity: ${ex.getMessage}")
          }
        }
      }
    }
  }
  protected val readRoute = path(basePath / LongNumber) {
    (id) => {
      get {
        onComplete(repository.findById(id)) {
          case Success(entityOption) => entityOption match {
            case Some(entity) => complete(entity)
            case None => complete(NotFound, s"Missing entity with id = $id")
          }
          case Failure(ex) => complete(InternalServerError, s"Some error happen while reading entity: ${ex.getMessage}")
        }
      }
    }
  }
  protected val readAllRoute = path(basePath / "all") {
    get {
      onComplete(repository.findAll) {
        case Success(entities) => complete(entity)
        case Failure(ex) => complete(InternalServerError, s"Some error happen while reading all entities: ${ex.getMessage}")
      }
    }
  }
  protected val updateRoute = path(basePath / LongNumber) {
    (id) => {
      put {
        entity(as[E]) {
          (e) => {
            onComplete(repository.updateById(id, e)) {
              case Success(_) => complete(OK)
              case Failure(ex) => complete(InternalServerError, s"Some error happen while entity updating: ${ex.getMessage}")
            }
          }
        }
      }
    }
  }
  protected val deleteRoute = path(basePath / LongNumber) {
    (id) => {}
      delete {
        onComplete(repository.deleteById(id)) {
          case Success(_) => complete(OK)
          case Failure(ex) => complete(InternalServerError, s"Some error happen while deleting entity: ${ex.getMessage}")
        }
      }
  }

  // TODO review if it's really needed or not
  protected val routes = createRoute ~ readRoute ~ readAllRoute ~ updateRoute ~ deleteRoute

  // It's not nice but allows to reduce boiler plate
  abstract val completeReadEntityRoute: Route

  // It's not nice but allows to reduce boiler plate
  abstract val completeReadEntitiesRoute: Route
}
