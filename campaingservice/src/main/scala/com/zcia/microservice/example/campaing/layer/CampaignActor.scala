package com.zcia.microservice.example.campaing.layer

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}

import scala.concurrent.ExecutionContextExecutor

object CampaignActor{
  case object Initialize
  case object Idle
  case class State(members: Option[Seq[Member]]=None, content: Option[Content]=None, list: Option[CampaignList]=None , campaign: Option[Campaign]=None, hasMember : Boolean = false, hasContent: Boolean = false)
}

// TODO asses to move to FSM instead
class CampaignActor(mailchimp: ActorRef, datalayer: ActorRef, settings:CampaignSettings) extends Actor with ActorLogging{

  var state = new CampaignActor.State()

  protected val system: ActorSystem = context.system
  protected implicit val executor : ExecutionContextExecutor = context.dispatcher

  override def receive: Receive = {
    case CampaignActor.Initialize => {
      datalayer ! new DataLayerActor.TopNRequest(settings.category,settings.memberSettings.num,settings.memberSettings.best,DataLayerActor.Subscribers)
      datalayer ! new DataLayerActor.TopNRequest(settings.category,settings.contentSettings.num,settings.contentSettings.best,DataLayerActor.Products)
      mailchimp ! new MailchimpActor.CreateList(settings.list)
    }
    case CampaignActor.Idle => {
      log.info(s"State updated: $state")
      if(state.members.nonEmpty && state.content.nonEmpty && state.list.nonEmpty ) {
        if(state.campaign.isEmpty){
          mailchimp ! new MailchimpActor.CreateCampaign(state.list.get.id,settings.campaign)
          mailchimp ! new MailchimpActor.AddMembers(state.list.get.id, state.members.get)
        } else {
          if(state.hasContent && state.hasMember) {
            mailchimp ! new MailchimpActor.SendCampaign(state.campaign.get.id)
          } else if (!state.hasContent) {
            mailchimp ! new MailchimpActor.AddCampaignContent(state.campaign.get.id,state.content.get)
          }
        }
      }
    }
    case subscribers: SubscriberAggregationsEmbedded => {
      val members = subscribers.embedded.subscribersAgg.map(s => new Member(s.email, "subscribed"))
      state=state.copy(members=Some(members))
      self ! CampaignActor.Idle
    }
    case products: ProductAggregationsEmbedded => {
      val variableContent : String = products.embedded.productAggs.map(p=>settings.contentSettings.variableFormat.format(p.name)).mkString("\n")
      val htmlContent : String = settings.contentSettings.contentFormat.format(variableContent)
      state=state.copy(content=Some(new Content(htmlContent)))
      self ! CampaignActor.Idle
    }
    case list: CampaignList => {
      state=state.copy(list=Some(list))
      self ! CampaignActor.Idle
    }
    case campaign: Campaign => {
      state=state.copy(campaign=Some(campaign))
      self ! CampaignActor.Idle
    }
    case event: MailchimpActor.AddedMembers => {
      state = state.copy(hasMember = true)
      self ! CampaignActor.Idle
    }
    case event: MailchimpActor.AddedCampaignContent => {
      state = state.copy(hasContent = true)
      self ! CampaignActor.Idle
    }
    case event: MailchimpActor.SentCampaign => {
      log.info("Success")
      context.stop(self)
    }
    case _ => {
      log.error("Unsupported event!")
      context.stop(self)
    }
  }
}
