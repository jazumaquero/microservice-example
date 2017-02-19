package com.zcia.microservice.example.campaing.layer


case class SubscriberAggregation(name: String, email: String, category: String, num: Long)
case class SubscriberAggregations(subscribersAgg: List[SubscriberAggregation])
case class SubscriberAggregationsEmbedded(embedded: SubscriberAggregations)
case class ProductAggregation(name: String, category: String, num: Long)
case class ProductAggregations(productAggs: List[ProductAggregation])
case class ProductAggregationsEmbedded(embedded:ProductAggregations)

trait DataLayerProtocol extends Protocol {
  implicit val subscriberAggregationFormat = jsonFormat(SubscriberAggregation,"subscriberName","subscriberEmail","categoryName","num")
  implicit val subscriberAggregationsSetFormat = jsonFormat(SubscriberAggregations, "subscribers_agg")
  implicit val subscriberAggregationEmbeddedFormat = jsonFormat(SubscriberAggregationsEmbedded,"_embedded")
  implicit val productAggregationFormat = jsonFormat(ProductAggregation,"productName","categoryName","num")
  implicit val productAggregationsSetFormat = jsonFormat(ProductAggregations, "products_agg")
  implicit val productAggregationEmbeddedFormat = jsonFormat(ProductAggregationsEmbedded,"_embedded")
}