package com.zcia.microservice.example.campaing.layer

import akka.actor.{Actor, ActorLogging, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.client.RequestBuilding._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.headers.BasicHttpCredentials
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.concurrent.{ExecutionContextExecutor, Future}

object MailchimpActor {
  sealed case class CreateList(list: CampaignList)
  sealed case class AddMembers(listId: String, members: Seq[Member])
  sealed case class AddedMembers(listId: String)
  sealed case class CreateCampaign(listId: String, campaign: Campaign)
  sealed case class AddCampaignContent(campaignId: String, content: Content)
  sealed case class AddedCampaignContent(campaignId: String)
  sealed case class SendCampaign(campaignId: String)
  sealed case class SentCampaign(campaignId: String)
  case object Error
}

class MailchimpActor extends Actor with ActorLogging with Protocol with Config {

  protected val host: String = mailchimpConfig.getString("host")
  protected val version = mailchimpConfig.getString("version")
  protected val apiKey = mailchimpConfig.getString("apikey")

  protected val system: ActorSystem = context.system
  protected implicit val executor : ExecutionContextExecutor = context.dispatcher
  protected implicit val materializer: ActorMaterializer = ActorMaterializer()

  protected val connection = Http(system).outgoingConnection(host)

  override def receive: Receive = {
    // Create list
    case event: MailchimpActor.CreateList => {
      log.debug(s"Creating list with given parameters: ${event.list}")
      sendToMailchimp(RequestBuilding.Post(s"/$version/lists", event.list)) map { response =>
        Unmarshal(response.entity).to[CampaignList] map { list =>
          log.info(s"Created campaign list: $list")
          sender ! list
        } recover {
          case _ => {
            log.error("Error while unmarshalling received campaign list.")
            sender ! MailchimpActor.Error
          }
        }
      } recover {
        case _ => {
          log.error("Error while requesting for a new campaign list.")
          sender ! MailchimpActor.Error
        }
      }
    }
    // Add member to some list
    case event: MailchimpActor.AddMembers => {
      log.debug(s"Adding members list with id ${event.listId}: ${event.members}")
      sendToMailchimp(RequestBuilding.Post(s"/$version/lists/${event.listId}", new Members(event.members))) map { response =>
        response.status match {
          case Created | OK => {
            log.info(s"Added members to list ${event.listId}.")
            sender ! new MailchimpActor.AddedMembers(event.listId)
          }
          case _ => {
            log.error(s"Error while processing adding members list: ${response}")
            sender ! MailchimpActor.Error
          }
        }
      } recover {
        case _ => {
          log.error("Error while requesting for adding members list.")
          sender ! MailchimpActor.Error
        }
      }
    }
    // Create campaign
    case event: MailchimpActor.CreateCampaign => {
      log.debug(s"Creating campaign with list id ${event.listId}: ${event.campaign}")
      sendToMailchimp(RequestBuilding.Post(s"/$version/campaigns", event.campaign)) map { response =>
        Unmarshal(response.entity).to[Campaign] map { campaign =>
          log.info(s"Created campaign: $campaign")
          sender ! campaign
        } recover {
          case _ => {
            log.error("Error while unmarshalling created campaign")
            sender ! MailchimpActor.Error
          }
        }
      } recover {
        case _ => {
          log.error("Error while request creating a new campaign.")
          sender ! MailchimpActor.Error
        }
      }
    }
    // Adding content to some created campaign
    case event: MailchimpActor.AddCampaignContent => {
      log.debug(s"Adding content to campaign ${event.campaignId} : ${event.content}")
      sendToMailchimp(RequestBuilding.Put(s"/$version/campaigns/${event.campaignId}/content", event.content)) map { response =>
        response.status match {
          case OK | Created => sender ! new MailchimpActor.AddedCampaignContent(event.campaignId)
          case _ => {
            log.error(s"Error while processing for adding content to campaign: ${response}")
            sender ! MailchimpActor.Error
          }
        }
      } recover {
        case _ => {
          log.error(s"Error while requesting for adding content to campaign: ${event.campaignId}")
          sender ! MailchimpActor.Error
        }
      }
    }
    // Send some created campaign
    case event: MailchimpActor.SendCampaign => {
      log.debug(s"Sending campaign ${event.campaignId}!")
      sendToMailchimp(RequestBuilding.Post(s"/$version/campaing/${event.campaignId}/actions/send")) map { response =>
        response.status match {
          case OK => sender ! MailchimpActor.SentCampaign(event.campaignId)
          case _ => {
            log.error(s"Error while processing for sending campaign: ${response}")
            sender ! MailchimpActor.Error
          }
        }
      } recover {
        case _ => {
          log.error(s"Error while requesting for sending campaign: ${event.campaignId}")
          sender ! MailchimpActor.Error
        }
      }
    }
    // Unknown message won't be accepted!
    case _ => {
      log.error("Unknown request!")
      sender ! MailchimpActor.Error
    }
  }

  protected def sendToMailchimp(request: HttpRequest): Future[HttpResponse] = {
    val credentials = BasicHttpCredentials("apikey", apiKey)
    log.debug(s"Requesting following: $request")
    Source.single(request ~> addCredentials(credentials)).via(connection).runWith(Sink.head)
  }
}
