package domain

import slick.driver.H2Driver.api._
import slick.lifted.Tag

case class Category(name: String)

case class CategoryEntity(name: String, id: Long)

class Categories(tag: Tag) extends Table[CategoryEntity](tag, "EC.CATEGORIES") {
  def id = column[Long]("catID")
  def name = column[String]("name")
  def * = (id, name) <> (CategoryEntity.tupled, CategoryEntity.unapply)
}