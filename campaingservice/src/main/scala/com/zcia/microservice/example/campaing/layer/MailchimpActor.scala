package com.zcia.microservice.example.campaing.layer

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.{ExecutionContextExecutor, Future}


object MailchimpActor {
  sealed case class CreateList(list: CampaignList)
  sealed case class AddMembers(listId: String, members: Seq[Member])
  case object AddedMembers
  sealed case class CreateCampaign(listId: String, campaign: Campaign)
  sealed case class AddCampaignContent(campaignId: String, content: Content)
  case object AddedCampaignContent
  sealed case class SendCampaign(campaignId: String)
  case object SentCampaign
  case object Error
}

class MailchimpActor extends Actor with ActorLogging with MailchimpProtocol with MailchimpConfig {

  import MailchimpActor._

  protected val system: ActorSystem = context.system
  protected implicit val executor : ExecutionContextExecutor = context.dispatcher
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  protected val connection = Http(system).outgoingConnection(host)

  override def receive: Receive = {
    case event: CreateList => createList(sender, event.list)
    case event: AddMembers => addMembers(sender,event.listId,event.members)
    case event: CreateCampaign => createCampaign(sender, event.listId, event.campaign)
    case event: AddCampaignContent => addCampaignContent(sender, event.campaignId, event.content)
    case event: SendCampaign => sendCampaign(sender, event.campaignId)
    case _ =>
      log.error("Unknown request!")
      sender ! MailchimpActor.Error
  }

  protected def sendToMailchimp(request: HttpRequest): Future[HttpResponse] = {
    val credentials = BasicHttpCredentials("apikey", apiKey)
    log.debug(s"Requesting following: $request")
    Source.single(request ~> addCredentials(credentials)).via(connection).runWith(Sink.head)
  }

  protected def createList(requestor: ActorRef, list: CampaignList): Unit = {
    log.debug(s"Creating list with given parameters: $list")
    sendToMailchimp(RequestBuilding.Post(s"/$version/lists", list)) map { response =>
      log.debug(s"Response for creating campaign list: $response")
      log.debug(s"Received response for create list: $response")
      Unmarshal(response.entity).to[CampaignList] map { list =>
        log.info(s"Created campaign list: $list")
        requestor ! list
      } recover {
        case _ =>
          log.error("Error while unmarshalling received campaign list.")
          requestor ! MailchimpActor.Error
      }
    } recover {
      case _ =>
        log.error("Error while requesting for a new campaign list.")
        requestor ! MailchimpActor.Error
    }
  }

  protected def addMembers(requestor: ActorRef, listId: String,members: Seq[Member] ): Unit = {
    log.debug(s"Adding members list with id $listId: $members")
    sendToMailchimp(RequestBuilding.Post(s"/$version/lists/$listId", Members(members))) map { response =>
      log.debug(s"Response for adding members to list: $response")
      response.status match {
        case Created | OK =>
          log.info(s"Added members to list $listId.")
          requestor ! MailchimpActor.AddedMembers
        case _ =>
          log.error(s"Error while processing adding members list: $response")
          requestor ! MailchimpActor.Error
      }
    } recover {
      case _ =>
        log.error("Error while requesting for adding members list.")
        requestor ! MailchimpActor.Error
    }
  }

  protected def createCampaign(requestor: ActorRef, listId: String, campaign: Campaign) : Unit = {
    log.debug(s"Creating campaign with list id $listId: $campaign")
    val requestedCampaign = campaign.copy(recipients = Recipient(listId))
    sendToMailchimp(RequestBuilding.Post(s"/$version/campaigns", requestedCampaign)) map { response =>
      log.debug(s"Response for creating campaign: $response")
      Unmarshal(response.entity).to[Campaign] map { campaign =>
        log.info(s"Created campaign: $campaign")
        requestor ! campaign
      } recover {
        case _ =>
          log.error("Error while unmarshalling created campaign")
          requestor ! MailchimpActor.Error
      }
    } recover {
      case _ =>
        log.error("Error while request creating a new campaign.")
        requestor ! MailchimpActor.Error
    }
  }

  protected def addCampaignContent(requestor: ActorRef, campaignId: String, content: Content) : Unit = {
    log.debug(s"Adding content to campaign $campaignId : $content")
    sendToMailchimp(RequestBuilding.Put(s"/$version/campaigns/$campaignId/content", content)) map { response =>
      log.debug(s"Response for adding campaign content: $response")
      response.status match {
        case OK | Created => requestor ! MailchimpActor.AddedCampaignContent
        case _ =>
          log.error(s"Error while processing for adding content to campaign: $response")
          requestor ! MailchimpActor.Error
      }
    } recover {
      case _ =>
        log.error(s"Error while requesting for adding content to campaign: $campaignId")
        requestor ! MailchimpActor.Error
    }
  }

  protected def sendCampaign(requestor: ActorRef, campaignId: String) : Unit = {
    log.debug(s"Sending campaign $campaignId!")
    sendToMailchimp(RequestBuilding.Post(s"/$version/campaing/$campaignId/actions/send")) map { response =>
      log.debug(s"Response for request sending campaign: $response")
      response.status match {
        case OK => requestor ! MailchimpActor.SentCampaign
        case _ =>
          log.error(s"Error while processing for sending campaign: $response")
          requestor ! MailchimpActor.Error
      }
    } recover {
      case _ =>
        log.error(s"Error while requesting for sending campaign: $campaignId")
        requestor ! MailchimpActor.Error
    }
  }
}
