package com.zcia.microservice.example.campaing.layer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.{ContentTypes, HttpRequest, HttpResponse, headers}
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future

trait MailchimpClient  extends BaseService with Protocol{

  protected implicit def system: ActorSystem
  protected implicit def materializer: ActorMaterializer

  protected val client = Http(system).outgoingConnection(mailchimpConfig.getString("host"))
  protected val apiKey = mailchimpConfig.getString("apikey")

  def sendToMailchimp(request: HttpRequest) : Future[HttpResponse] = {
    Source.single(request.withHeaders(headers.`Content-Type`(ContentTypes.`application/json`))~> addCredentials(BasicHttpCredentials(apiKey))).via(client).runWith(Sink.head)
  }

  def createList(list: MailchimpDomain.List) : Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post("/list",list))
  }

  def addMembersToList(list: MailchimpDomain.List,members: Seq[MailchimpDomain.Member]): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post(s"/list/${list.id}/members",members))
  }

  def createCampaign(campaign: MailchimpDomain.Campaign): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post("/campaigns",campaign))
  }

  def createCampaignContent(campaign: MailchimpDomain.Campaign, content: MailchimpDomain.Content): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Put(s"/campaigns/${campaign.id}/content",content))
  }

  def sendCampaing(campaign: MailchimpDomain.Campaign): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post(s"/campaing/${campaign.id}/actions/send"))
  }
  
}
