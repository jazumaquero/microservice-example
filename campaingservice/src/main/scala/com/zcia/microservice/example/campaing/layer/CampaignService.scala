package com.zcia.microservice.example.campaing.layer

import akka.actor.Actor
import akka.http.scaladsl.unmarshalling.Unmarshal

object CampaignService {
  sealed case class Initialize()
  sealed case class CreateList(list: CampaignList)
  sealed case class AddMembers(list: CampaignList, members: Seq[Member])
  sealed case class CreateCampaign(list: CampaignList, campaign: Campaign)
  sealed case class AddCampaignContent(campaign: Campaign, content: Content)
  sealed case class SendCampaign(campaign: Campaign)
}

class CampaignService(
                       serviceList: CampaignList,
                       serviceMembers: Seq[Member],
                       serviceCampaign: Campaign,
                       serviceContent: Content
                     ) extends Actor with MailchimpClient with System.LoggerExecutor {

  protected implicit def materializer = System.materializer
  protected implicit def system = System.system

  import CampaignService._

  override def receive: Receive = {
    case request: Initialize => {
      self ! new CreateList(serviceList)
    }
    case request: CreateList => {
      createList(request.list) map { response =>
        Unmarshal(response.entity).to[CampaignList] map { list =>
          self ! new AddMembers(list, serviceMembers)
        } recover {
          case _ => failed("Fail while un-marshalling created list")
        }
      } recover {
        case _ => failed("Fail requesting for a new list")
      }
    }
    case request: AddMembers => {
      addMembersToList(request.list, request.members) map { response =>
        self ! new CreateCampaign(request.list, getConfiguredCampaing(request.list))
      } recover {
        case _ => failed("Fail while adding members to list")
      }
    }
    case request: CreateCampaign => {
      createCampaign(request.campaign) map { response =>
        Unmarshal(response.entity).to[Campaign] map { campaign =>
          self ! new AddCampaignContent(campaign, serviceContent)
        } recover {
          case _ => failed("Fail while un-marshalling created campaign")
        }
      } recover {
        case _ => failed("Fail while requesting for a new campaign")
      }
    }
    case request: AddCampaignContent => {
      createCampaignContent(request.campaign, request.content) map { response =>
        self ! SendCampaign(request.campaign)
      } recover {
        case _ => failed("Fail while adding content to campaign")
      }
    }
    case request: SendCampaign => {
      sendCampaing(request.campaign) map { response =>
        log.info("Success!")
        context.stop(self)
      }
    }
  }

  def failed(trace: String): Unit = {
    log.error(s"$trace")
    context.stop(self)
  }

  def getConfiguredCampaing(list: CampaignList) = {
    serviceCampaign.copy(recipients =  new Recipient(list.id))
  }
}