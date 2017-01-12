package rest

import domain.{Product, ProductEntity, Subscriber, SubscriberEntity}
import spray.json.DefaultJsonProtocol

object Protocols extends DefaultJsonProtocol {
  implicit val subscribersFormat = jsonFormat3(SubscriberEntity)
  implicit val subscribersDtoFormat = jsonFormat2(Subscriber)
  implicit val productsFormat = jsonFormat3(ProductEntity)
  implicit val productsDtoFormat = jsonFormat2(Product)
}
