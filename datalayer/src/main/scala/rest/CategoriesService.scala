package rest

<<<<<<< Updated upstream
import akka.http.scaladsl.server.Route
import dao.BaseRepository
import domain.{Categories, CategoryEntity}
import slick.lifted.TableQuery
import spray.json.DefaultJsonProtocol

object CategoriesProtocol extends DefaultJsonProtocol{
=======
import dao.CategoriesRepository

/*
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import dao.{BaseRepository, CategoriesRepository}
import domain.{Categories, Category, CategoryEntity}
import spray.json.DefaultJsonProtocol

>>>>>>> Stashed changes

object CategoriesProtocol extends DefaultJsonProtocol {
  implicit val categoriesFormat = jsonFormat2(CategoryEntity)
  implicit val simpleCategoriesFormat = jsonFormat1(Category)
}

<<<<<<< Updated upstream
class CategoriesService extends BaseCrudService[CategoryEntity,Categories] {
=======
class CategoriesService extends BaseCrudService[CategoryEntity, Categories] {
>>>>>>> Stashed changes
  override val basePath: String = "categories"
  override val repository: BaseRepository[Categories, CategoryEntity] = new BaseRepository[Categories, CategoryEntity](TableQuery[Categories]) {}

  // It's not nice but allows to reduce boiler plate
<<<<<<< Updated upstream
  override abstract def completeReadEntityRoute(entity: CategoryEntity): Route =
=======
  override abstract def entityToResponse(entity: CategoryEntity): ToResponseMarshallable = ???
>>>>>>> Stashed changes

  // It's not nice but allows to reduce boiler plate
  override abstract def entitiesToResponse(entities: Seq[CategoryEntity]): ToResponseMarshallable = ???
}
*/
class CategoriesService{
  val repository = new CategoriesRepository
}
