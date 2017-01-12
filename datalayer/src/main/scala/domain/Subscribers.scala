package domain

import slick.driver.H2Driver.api._
import slick.lifted.Tag

case class Subscriber(name: String, email: String)

case class SubscriberEntity(name: String, email: String, id: Long)

case class Subscribers(tag: Tag) extends Table[SubscriberEntity](tag, "EC.SUBSCRIBERS") {
  def id = column[Long]("subID")
  def name = column[String]("name")
  def email = column[String]("email")
  def * = (id, name, email) <> (SubscriberEntity.tupled, SubscriberEntity.unapply)
}