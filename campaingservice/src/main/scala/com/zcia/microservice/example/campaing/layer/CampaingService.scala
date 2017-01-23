package com.zcia.microservice.example.campaing.layer

import java.io.IOException

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes.{BadRequest, OK, Created}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.concurrent.Future

trait CampaingService extends BaseService with DatalayerClient with MailchimpClient{

  protected val eventRoutes =???
}
