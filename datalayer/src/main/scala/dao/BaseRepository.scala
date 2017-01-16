package dao

import slick.driver.PostgresDriver
import slick.driver.PostgresDriver.api._
import slick.lifted.{CanBeQueryCondition, Rep, TableQuery, Tag}

import scala.concurrent.Future
import scala.reflect._


object DriverHelper {
  // TODO this module must be updated with config instead of hardconding
  val user = "postgres"
  val url = "jdbc:postgresql://localhost:5432/LocalDB"
  val password = "admin"
  val jdbcDriver = "org.postgresql.Driver"
  val db = Database.forURL(url, user, password, driver = jdbcDriver)
}

trait BaseEntity {
  val id: Long
}

abstract class BaseTable[E: ClassTag](tag: Tag, schemaName: Option[String], tableName: String) extends Table[E](tag, schemaName, tableName) {
  val classOfEntity = classTag[E].runtimeClass
  val id: Rep[Long] = column[Long]("Id", O.PrimaryKey, O.AutoInc)
}

trait BaseRepositoryComponent[T <: BaseTable[E], E <: BaseEntity] {
  def findById(id: Long): Future[Option[E]]

  def findAll: Future[Seq[E]]

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]]

  def save(row: E): Future[E]

  def deleteById(id: Long): Future[Int]

  def updateById(id: Long, row: E): Future[Int]
}

abstract class BaseRepository[T <: BaseTable[E], E <: BaseEntity : ClassTag](tableQuery: TableQuery[T]) extends BaseRepositoryComponent[T, E] {

  lazy val clazzEntity = classTag[E].runtimeClass
  val clazzTable: TableQuery[T] = tableQuery
  val query: PostgresDriver.api.type#TableQuery[T] = tableQuery
  val db: PostgresDriver.backend.DatabaseDef = DriverHelper.db

  def findAll: Future[Seq[E]] = {
    db.run(query.result)
  }

  def findById(id: Long): Future[Option[E]] = {
    db.run(query.filter(_.id === id).result.headOption)
  }

  def filter[C <: Rep[_]](expr: T => C)(implicit wt: CanBeQueryCondition[C]): Future[Seq[E]] = {
    db.run(query.withFilter(expr).result)
  }

  def save(row: E): Future[E] = {
    db.run(query returning query += row)
  }

  def updateById(id: Long, row: E): Future[Int] = {
    db.run(query.filter(_.id === id).update(row))
  }

  def deleteById(id: Long): Future[Int] = {
    db.run(query.filter(_.id === id).delete)
  }

}