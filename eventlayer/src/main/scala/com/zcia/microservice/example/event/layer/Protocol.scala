package com.zcia.microservice.example.event.layer

import spray.json.DefaultJsonProtocol

case class BuyEvent(productId: Long, subscriberId: Long)
case class LoginEvent(name:String, email:String)

case class SubscriberAggregation(name: String, email: String, category: String, num: Long)
case class SubscriberAggregations(subscribersAgg: List[SubscriberAggregation])
case class SubscriberAggregationsEmbedded(embedded: SubscriberAggregations)
case class ProductAggregation(name: String, category: String, num: Long)
case class ProductAggregations(productAggs: List[ProductAggregation])
case class ProductAggregationsEmbedded(embedded:ProductAggregations)

trait Protocol extends DefaultJsonProtocol {
  implicit val buyEventFormatter = jsonFormat2(BuyEvent)
  implicit val loginEventFormatter = jsonFormat2(LoginEvent)
  implicit val subscriberAggregationFormat = jsonFormat(SubscriberAggregation,"subscriberName","subscriberEmail","categoryName","num")
  implicit val subscriberAggregationsSetFormat = jsonFormat(SubscriberAggregations, "subscribers_agg")
  implicit val subscriberAggregationEmbeddedFormat = jsonFormat(SubscriberAggregationsEmbedded,"_embedded")
  implicit val productAggregationFormat = jsonFormat(ProductAggregation,"productName","categoryName","num")
  implicit val productAggregationsSetFormat = jsonFormat(ProductAggregations, "products_agg")
  implicit val productAggregationEmbeddedFormat = jsonFormat(ProductAggregationsEmbedded,"_embedded")
}