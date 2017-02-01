package com.zcia.microservice.example.campaing.layer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, HttpResponse, headers}
import akka.http.scaladsl.model.headers.{BasicHttpCredentials, GenericHttpCredentials, HttpCredentials}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

trait MailchimpClient  extends BaseService with Protocol{

  protected implicit def system: ActorSystem
  protected implicit def materializer: ActorMaterializer

  protected val client = Http(system).outgoingConnection(mailchimpConfig.getString("host"))
  protected val version = mailchimpConfig.getString("version")
  protected val apiKey = mailchimpConfig.getString("apikey")

  protected def sendToMailchimp(request: HttpRequest) : Future[HttpResponse] = {
    Source.single(request~> addCredentials(BasicHttpCredentials("apikey",apiKey))).via(client).runWith(Sink.head)
  }

  def createList(list: CampaignList) : Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post(s"/$version/lists",list))
  }

  def addMembersToList(list: CampaignList, members: Seq[Member]): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post(s"/$version/lists/${list.id}/members",members))
  }

  def createCampaign(campaign: Campaign): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post(s"/$version/campaigns",campaign))
  }

  def createCampaignContent(campaign: Campaign, content: Content): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Put(s"/$version/campaigns/${campaign.id}/content",content))
  }

  def sendCampaing(campaign: Campaign): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post(s"/$version/campaing/${campaign.id}/actions/send"))
  }

}
