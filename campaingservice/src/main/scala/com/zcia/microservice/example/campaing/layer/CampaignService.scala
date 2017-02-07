package com.zcia.microservice.example.campaing.layer

import akka.actor.Actor
import akka.http.scaladsl.unmarshalling.Unmarshal

object CampaignService {
  sealed case class Initialize()
  sealed case class CreateList(list: CampaignList)
  sealed case class GetMembers(list: CampaignList)
  sealed case class AddMembers(list: CampaignList, members: Seq[Member])
  sealed case class CreateCampaign(list: CampaignList, campaign: Campaign)
  sealed case class GetCampaignContent(campaign: Campaign)
  sealed case class AddCampaignContent(campaign: Campaign, content: Content)
  sealed case class SendCampaign(campaign: Campaign)
}

class CampaignService(
                       serviceList: CampaignList,
                       serviceMembers: Seq[Member],
                       serviceCampaign: Campaign,
                       serviceContent: Content
                     ) extends Actor with MailchimpClient with DatalayerClient with System.LoggerExecutor {

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
          self ! new GetMembers(list)
        } recover {
          case _ => failed("Fail while un-marshalling created list")
        }
      } recover {
        case _ => failed("Fail requesting for a new list")
      }
    }
    case request: GetMembers => {
      // TODO obtain this val fron config/endpoint
      val category: String =""
      val numSubscribers: Integer = 10
      val best: Boolean = true
      getTopNSubscribers(category, numSubscribers, best) map { response =>
        Unmarshal(response.entity).to[Seq[SubscriberAggregations]] map { subscribers =>
          self ! new AddMembers(request.list, subscribers.map(s => new Member(s.email,"subscribed")))
        } recover {
          case _=> failed("Fail while un-marshalling list of subscribers")
        }
      } recover {
        case _ => failed("Fail requesting for subscribers")
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
          self ! new GetCampaignContent(campaign)
        } recover {
          case _ => failed("Fail while un-marshalling created campaign")
        }
      } recover {
        case _ => failed("Fail while requesting for a new campaign")
      }
    }
    case request: GetCampaignContent => {
      // TODO obtain this val fron config/endpoint
      val category: String =""
      val numProducts: Integer = 5
      val best: Boolean = false
      getTopNProducts(category,numProducts,best) map { response =>
        Unmarshal(response.entity).to[Seq[ProductAggregations]] map { products =>
          // TODO obtain this val fron config/endpoint
          val variableContent : String = products.map(p=>s"<li>${p.name}</li>").mkString("\n")
          val content : String = s"<body><p>No se olviden de traer la siguiente lista de la compra</p><ul>$variableContent</ul></body>"
          self ! new AddCampaignContent(request.campaign, new Content(content))
        } recover {
          case _ => failed("Fail while un-marshalling products")
        }
      } recover {
        case _ => failed("Fail while requesting for products")
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