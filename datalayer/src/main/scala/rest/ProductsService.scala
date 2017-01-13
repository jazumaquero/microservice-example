package rest

import akka.http.scaladsl.server.Route
import dao.{BaseRepository, ProductsRepository}
import domain.{ProductEntity, Products}
import spray.json.DefaultJsonProtocol

trait ProductsProtocol extends DefaultJsonProtocol {

}

class ProductsService extends BaseCrudService[ProductEntity, Products] {
  override val basePath: String = "categories"
  override val repository: BaseRepository[Products, ProductEntity] = new ProductsRepository

  // It's not nice but allows to reduce boiler plate
  override abstract def completeReadEntityRoute(entity: ProductEntity): Route = ???

  // It's not nice but allows to reduce boiler plate
  override abstract def completeReadEntitiesRoute(entities: Seq[ProductEntity]): Route = ???
}