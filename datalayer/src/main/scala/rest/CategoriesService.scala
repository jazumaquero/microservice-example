package rest

import akka.http.scaladsl.server.Route
import dao.{BaseRepository, CategoriesRepository}
import domain.{Categories, CategoryEntity}
import spray.json.DefaultJsonProtocol

trait CategoriesProtocol extends DefaultJsonProtocol{

}

class CategoriesService extends BaseCrudService[CategoryEntity,Categories] with CategoriesProtocol{
  override val basePath: String = "categories"
  override val repository: BaseRepository[Categories, CategoryEntity] = new CategoriesRepository

  // It's not nice but allows to reduce boiler plate
  override abstract def completeReadEntityRoute(entity: CategoryEntity): Route = ???

  // It's not nice but allows to reduce boiler plate
  override abstract def completeReadEntitiesRoute(entities: Seq[CategoryEntity]): Route = ???
}
