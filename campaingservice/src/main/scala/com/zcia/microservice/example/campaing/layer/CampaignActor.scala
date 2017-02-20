package com.zcia.microservice.example.campaing.layer

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}

import scala.concurrent.ExecutionContextExecutor


object CampaignActor{
  case object Initialize
  case object Idle
  case class State(members: Option[Seq[Member]]=None, content: Option[Content]=None, list: Option[CampaignList]=None , campaign: Option[Campaign]=None, hasMember : Boolean = false, hasContent: Boolean = false) {
    def ready : Boolean = members.nonEmpty && content.nonEmpty && campaign.nonEmpty && hasMember && hasContent
  }
}

class CampaignActor(mailchimp: ActorRef, datalayer: ActorRef, settings:CampaignSettings) extends Actor with ActorLogging {
  protected val system: ActorSystem = context.system
  protected implicit val executor : ExecutionContextExecutor = context.dispatcher

  protected var state = CampaignActor.State()

  override def receive: Receive = {
    case CampaignActor.Initialize =>
      datalayer ! DataLayerActor.TopNRequest(settings.category,settings.memberSettings.num,settings.memberSettings.best,DataLayerActor.Subscribers)
      datalayer ! DataLayerActor.TopNRequest(settings.category,settings.contentSettings.num,settings.contentSettings.best,DataLayerActor.Products)
      mailchimp ! MailchimpActor.CreateList(settings.list)
    case CampaignActor.Idle =>
      log.info(s"State updated: $state")
      if(state.ready)
        mailchimp ! MailchimpActor.SendCampaign(state.campaign.get.id)
    case subscribers: SubscriberAggregations =>
      val members = subscribers.items.map(s => Member(s.email, "subscribed"))
      log.info(s"Campaign members ready: $members")
      state=state.copy(members=Some(members))
      self ! CampaignActor.Idle
    case products: ProductAggregations =>
      val variableContent : String = products.items.map(p=>settings.contentSettings.variableFormat.format(p.name)).mkString("\n")
      val htmlContent : String = settings.contentSettings.contentFormat.format(variableContent)
      log.info(s"Campaign content ready: $htmlContent")
      state=state.copy(content=Some(Content(htmlContent)))
      self ! CampaignActor.Idle
    case list: CampaignList =>
      log.info(s"Campaign list ready: $list")
      state=state.copy(list=Some(list))
      mailchimp ! MailchimpActor.CreateCampaign(state.list.get.id,settings.campaign)
      mailchimp ! MailchimpActor.AddMembers(state.list.get.id, state.members.get)
      self ! CampaignActor.Idle
    case campaign: Campaign =>
      log.info(s"Campaign  ready: $campaign")
      state=state.copy(campaign=Some(campaign))
      mailchimp ! MailchimpActor.AddCampaignContent(state.campaign.get.id,state.content.get)
      self ! CampaignActor.Idle
    case MailchimpActor.AddedMembers =>
      log.info(s"Campaign members added!")
      state = state.copy(hasMember = true)
      self ! CampaignActor.Idle
    case MailchimpActor.AddedCampaignContent =>
      log.info(s"Campaign content added!")
      state = state.copy(hasContent = true)
      self ! CampaignActor.Idle
    case MailchimpActor.SentCampaign =>
      log.info("Success while sending campaign")
      context.stop(self)
    case _ =>
      log.error(s"Unsupported event from ${sender.path}!")
      context.stop(self)
  }
}
