package rest

import akka.http.scaladsl.server.Route
import dao.BaseRepository
import domain.{Product, ProductEntity, Products}
import spray.json.DefaultJsonProtocol

object ProductsProtocol extends DefaultJsonProtocol {
  implicit  val productFormat = jsonFormat3(ProductEntity)
  implicit val simpleProductFormat = jsonFormat2(Product)
}

class ProductsService extends BaseCrudService[ProductEntity, Products] {
  override val basePath: String = "categories"
  override val repository: BaseRepository[Products, ProductEntity] = new ProductsRepository

  import ProductsProtocol._

  override abstract val completeReadEntityRoute: Route = complete(e)
  override abstract val completeReadEntitiesRoute: Route = _
}