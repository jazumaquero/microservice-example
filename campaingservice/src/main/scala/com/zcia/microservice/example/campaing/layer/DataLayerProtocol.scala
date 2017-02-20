package com.zcia.microservice.example.campaing.layer


case class SubscriberAggregation(name: String, email: String, category: String, num: Long)
case class SubscriberAggregations(items: Seq[SubscriberAggregation])
case class ProductAggregation(name: String, category: String, num: Long)
case class ProductAggregations(items: Seq[ProductAggregation])

trait DataLayerProtocol extends Protocol {
  implicit val subscriberAggregationFormat = jsonFormat(SubscriberAggregation,"subscriberName","subscriberEmail","categoryName","num")
  implicit val productAggregationFormat = jsonFormat(ProductAggregation,"productName","categoryName","num")
}