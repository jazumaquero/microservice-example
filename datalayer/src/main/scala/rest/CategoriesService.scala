package rest

import akka.http.scaladsl.server.Route
import dao.BaseRepository
import domain.{Categories, CategoryEntity}
import slick.lifted.TableQuery
import spray.json.DefaultJsonProtocol

object CategoriesProtocol extends DefaultJsonProtocol{

}

class CategoriesService extends BaseCrudService[CategoryEntity,Categories] {
  override val basePath: String = "categories"
  override val repository: BaseRepository[Categories, CategoryEntity] = new BaseRepository[Categories, CategoryEntity](TableQuery[Categories]) {}

  // It's not nice but allows to reduce boiler plate
  override abstract def completeReadEntityRoute(entity: CategoryEntity): Route =

  // It's not nice but allows to reduce boiler plate
  override abstract def completeReadEntitiesRoute(entities: Seq[CategoryEntity]): Route = ???
}
