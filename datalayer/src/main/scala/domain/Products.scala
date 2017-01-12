package domain
import slick.driver.H2Driver.api._
import slick.lifted.Tag

case class Product(name: String, price: Float)

case class ProductEntity(name: String, price: Float, id: Long)

class Products(tag: Tag) extends Table[CategoryEntity](tag, "EC.PRODUCTS") {
  def id = column[Long]("productID")
  def name = column[String]("name")
  def price = column[Float]("price")
  def * = (id, name, price) <> (CategoryEntity.tupled, CategoryEntity.unapply)
}