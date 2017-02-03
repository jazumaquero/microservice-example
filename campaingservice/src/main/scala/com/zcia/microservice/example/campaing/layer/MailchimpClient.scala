package com.zcia.microservice.example.campaing.layer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait MailchimpClient extends BaseService with Protocol {

  protected implicit def system: ActorSystem
  protected implicit def materializer: ActorMaterializer

  protected val client = Http(system).outgoingConnection(mailchimpConfig.getString("host"))
  protected val version = mailchimpConfig.getString("version")
  protected val apiKey = mailchimpConfig.getString("apikey")

  protected def sendToMailchimp(request: HttpRequest): Future[HttpResponse] = {
    val credentials = BasicHttpCredentials("apikey", apiKey)
    Source.single(request ~> addCredentials(credentials)).via(client).runWith(Sink.head)
  }

  def createList(list: CampaignList): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post(s"/$version/lists", list))
  }

  def createList2(list: CampaignList): Either[CampaignList, Throwable] = {
    var result : Either[CampaignList, Throwable] = Left(list)
    createList(list) onComplete {
      case Success(response) => {
        val l = Unmarshal(response.entity).to[CampaignList]
        l onComplete {
          case Success(l) => result = Left(l)
          case Failure(ex) => result = Right(ex)
        }
      }
      case Failure(ex) => result = Right(ex)
    }
    result
  }

  def addMembersToList(list: CampaignList, members: Seq[Member]): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post(s"/$version/lists/${list.id}/members", members))
  }

  def addMembersToList2(list: CampaignList, members: Seq[Member]): Either[Unit, Throwable] = {
    var result : Either[Unit, Throwable] = Left(Unit)
    addMembersToList(list, members) onComplete {
      case Success(response) => Unit
      case Failure(ex) => result = Right(ex)
    }
    result
  }

  def createCampaign(campaign: Campaign): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post(s"/$version/campaigns", campaign))
  }

  def createCampaign2(campaign: Campaign): Either[Campaign, Throwable] = {
    var result : Either[Campaign, Throwable] = Left(campaign)
    createCampaign(campaign) onComplete {
      case Success(response) =>Unmarshal(response.entity).to[Campaign] onComplete {
        case Success(_) => Unit
        case Failure(ex) => Right(ex)
      }
      case Failure(ex) =>Right(ex)
    }
    result
  }

  def createCampaignContent(campaign: Campaign, content: Content): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Put(s"/$version/campaigns/${campaign.id}/content", content))
  }

  def createCampaignContent2(campaign: Campaign, content: Content): Either[Unit, Throwable] = {
    var result : Either[Unit, Throwable] = Left(Unit)
    createCampaignContent(campaign,content) onComplete {
      case Success(response) => Unit
      case Failure(ex) => result = Right(ex)
    }
    result
  }

  def sendCampaing(campaign: Campaign): Future[HttpResponse] = {
    sendToMailchimp(RequestBuilding.Post(s"/$version/campaing/${campaign.id}/actions/send"))
  }

  def sendCampaing2(campaign: Campaign): Either[Unit, Throwable] = {
    var result : Either[Unit, Throwable] = Left(Unit)
    sendCampaing(campaign) onComplete {
      case Success(response) => Unit
      case Failure(ex) => result = Right(ex)
    }
    result
  }

  /** TODO Following must be move to main**/
  protected val contactConfig = mailchimpConfig.getConfig("contact")
  protected val campaingConfig=mailchimpConfig.getConfig("campaign")
  protected val campaingDefaultsConfig=campaingConfig.getConfig("defaults")
  protected val campaingSettings=campaingConfig.getConfig("settings")

   def getConfiguredList(): CampaignList =  {
    val contact = new Contact(contactConfig.getString("company"),
      contactConfig.getString("address1"),
      contactConfig.getString("city"),
      contactConfig.getString("state"),
      contactConfig.getString("zip"),
      contactConfig.getString("country")
    )
    val defaults = new CampaignDefaults(campaingDefaultsConfig.getString("from_name"),
      campaingDefaultsConfig.getString("from_email"),
      campaingDefaultsConfig.getString("subject"),
      campaingDefaultsConfig.getString("language")
    )
    new CampaignList("foo", campaingConfig.getString("name"), contact, campaingConfig.getString("permission_reminder"), defaults )
  }

  def getMembers(): Seq[Member] = Seq()

   def getConfiguredCampaing(list: CampaignList): Campaign = {
    val recipients = new Recipient("")
    val settings = new Settings(subject_line=campaingSettings.getString("subject_line"),
      reply_to=campaingSettings.getString("reply_to"),
      from_name=campaingSettings.getString("from_name")
    )
    new Campaign("", recipients, campaingConfig.getString("type"), settings)
  }

   def getConfiguredContent(): Content = new Content("<p>Offuuu illo</p>")
}
