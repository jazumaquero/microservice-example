package com.zcia.microservice.example.campaing.layer

import akka.actor.Actor
import akka.http.scaladsl.unmarshalling.Unmarshal
import spray.json._

case class MemberSettings(num: Integer, best: Boolean)
case class ContentSettings(num: Integer, best: Boolean , variableFormat : String,contentFormat : String)
case class CampaignSettings(list: CampaignList, campaign: Campaign, memberSettings: MemberSettings, contentSettings: ContentSettings, category: String)

// TODO use single type of mutable message for any state
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

// TODO MailchimpClient and DatalayerClient should be passed as attributes, in order to use singlenton instances (consider using actors to deal into a distributed fashion)
class CampaignService(settings:CampaignSettings) extends Actor with MailchimpClient with DatalayerClient with System.LoggerExecutor {
  protected implicit def materializer = System.materializer
  protected implicit def system = System.system

  import CampaignService._

  override def receive: Receive = {
    case request: Initialize => {
      self ! new CreateList(settings.list)
    }
    case request: CreateList => {
      createList(request.list) map { response =>
        Unmarshal(response.entity).to[CampaignList] map { list =>
          log.info(s"Created campaign list: $list")
          self ! new GetMembers(list)
        } recover {
          case _ => failed("Fail while un-marshalling created list")
        }
      } recover {
        case _ => failed("Fail requesting for a new list")
      }
    }
    case request: GetMembers => {
      getTopNSubscribers(settings.category, settings.memberSettings.num, settings.memberSettings.best) map { response =>
        // FIXME this workaround that allows unmarshalling
        Unmarshal(response.entity).to[String] map { payload =>
          val subscribers_agg = payload.parseJson.convertTo[SubscriberAggregationsEmbedded]
          val members : Seq[Member] = subscribers_agg.embedded.subscribersAgg.map(s => new Member(s.email,"subscribed"))
          log.info(s"Recovered members: ${subscribers_agg.embedded.subscribersAgg}")
          self ! new AddMembers(request.list, members)
        } recover {
          case _=> failed("Fail while un-marshalling list of subscribers")
        }
      } recover {
        case _ => failed("Fail requesting for subscribers")
      }
    }
    case request: AddMembers => {
      addMembersToList(request.list, request.members) map { response =>
        log.info(s"Added members to campaign list: $request.members")
        self ! new CreateCampaign(request.list, getConfiguredCampaing(request.list))
      } recover {
        case _ => failed("Fail while adding members to list")
      }
    }
    case request: CreateCampaign => {
      createCampaign(request.campaign) map { response =>
        Unmarshal(response.entity).to[Campaign] map { campaign =>
          log.info(s"Created campaign: $campaign")
          self ! new GetCampaignContent(campaign)
        } recover {
          case _ => failed("Fail while un-marshalling created campaign")
        }
      } recover {
        case _ => failed("Fail while requesting for a new campaign")
      }
    }
    case request: GetCampaignContent => {
      getTopNProducts(settings.category,settings.contentSettings.num,settings.contentSettings.best) map { response =>
        // FIXME this workaround that allows unmarshalling
        Unmarshal(response.entity).to[String] map { payload =>
          val products_agg = payload.parseJson.convertTo[ProductAggregationsEmbedded]
          val variableContent : String = products_agg.embedded.productAggs.map(p=>settings.contentSettings.variableFormat.format(p.name)).mkString("\n")
          val content : String = settings.contentSettings.contentFormat.format(variableContent)
          log.info(s"Adding campaign content: $content")
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
      } recover {
        case _ => failed("Fail while sending campaign to members")
      }
    }
  }

  def failed(trace: String): Unit = {
    log.error(s"$trace")
    context.stop(self)
  }

  def getConfiguredCampaing(list: CampaignList) = {
    settings.campaign.copy(recipients =  new Recipient(list.id))
  }
}
