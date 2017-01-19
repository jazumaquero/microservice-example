package rest


import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives.{complete, _}
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
            case Failure(ex) => completeOnFailure("creating entity", ex)
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
<<<<<<< Updated upstream
            case Some(entity) => complete(entity)
=======
            case Some(entity) => complete(entityToResponse(entity))
>>>>>>> Stashed changes
            case None => complete(NotFound, s"Missing entity with id = $id")
          }
          case Failure(ex) => completeOnFailure("reading entity", ex)
        }
      }
    }
  }
  protected val readAllRoute = path(basePath / "all") {
    get {
      onComplete(repository.findAll) {
<<<<<<< Updated upstream
        case Success(entities) => complete(entity)
        case Failure(ex) => complete(InternalServerError, s"Some error happen while reading all entities: ${ex.getMessage}")
=======
        case Success(entities) => complete(entitiesToResponse(entities))
        case Failure(ex) => completeOnFailure("reading all entities", ex)
>>>>>>> Stashed changes
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
              case Failure(ex) => completeOnFailure("updating entity:", ex)
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
          case Failure(ex) => completeOnFailure("deleting entity:", ex)
        }
      }
  }
  // TODO review if it's really needed or not
  protected val routes = createRoute ~ readRoute ~ readAllRoute ~ updateRoute ~ deleteRoute

  def completeOnFailure(operation: String, ex: Throwable) = complete(InternalServerError, s"Some error happen while $operation: ${ex.getMessage}")

  // It's not nice but allows to reduce boiler plate
<<<<<<< Updated upstream
  abstract val completeReadEntityRoute: Route

  // It's not nice but allows to reduce boiler plate
  abstract val completeReadEntitiesRoute: Route
=======
  abstract def entityToResponse(entity: E): ToResponseMarshallable

  // It's not nice but allows to reduce boiler plate
  abstract def entitiesToResponse(entities: Seq[E]): ToResponseMarshallable
>>>>>>> Stashed changes
}
