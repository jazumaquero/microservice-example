package rest

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.server.Route
<<<<<<< Updated upstream
import dao.BaseRepository
=======
import dao.{BaseRepository, ProductsRepository}
>>>>>>> Stashed changes
import domain.{Product, ProductEntity, Products}
import spray.json.DefaultJsonProtocol

object ProductsProtocol extends DefaultJsonProtocol {
<<<<<<< Updated upstream
  implicit  val productFormat = jsonFormat3(ProductEntity)
  implicit val simpleProductFormat = jsonFormat2(Product)
=======
  implicit val produtcsFormat = jsonFormat3(ProductEntity)
  implicit val simpleProdutcsFormat = jsonFormat2(Product)
>>>>>>> Stashed changes
}

class ProductsService extends BaseCrudService[ProductEntity, Products] {
  override val basePath: String = "categories"
  override val repository: BaseRepository[Products, ProductEntity] = new ProductsRepository

<<<<<<< Updated upstream
  import ProductsProtocol._

  override abstract val completeReadEntityRoute: Route = complete(e)
  override abstract val completeReadEntitiesRoute: Route = _
=======
  // It's not nice but allows to reduce boiler plate
  override abstract def entityToResponse(entity: ProductEntity): ToResponseMarshallable = ???

  // It's not nice but allows to reduce boiler plate
  override abstract def entitiesToResponse(entities: Seq[ProductEntity]): ToResponseMarshallable = ???
>>>>>>> Stashed changes
}