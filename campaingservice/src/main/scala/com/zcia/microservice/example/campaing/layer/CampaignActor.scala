package com.zcia.microservice.example.campaing.layer

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}

import scala.concurrent.ExecutionContextExecutor

case class MemberSettings(num: Integer, best: Boolean)
case class ContentSettings(num: Integer, best: Boolean , variableFormat : String,contentFormat : String)
case class CampaignSettings(list: CampaignList, campaign: Campaign, memberSettings: MemberSettings, contentSettings: ContentSettings, category: String)

object CampaignActor{
  case object Initialize
  case object Idle
  case class State(members: Option[Seq[Member]]=None, content: Option[Content]=None, list: Option[CampaignList]=None , campaign: Option[Campaign]=None, hasMember : Boolean = false, hasContent: Boolean = false)
}

// TODO asses to move to FSM instead
class CampaignActor(mailchimp: ActorRef, datalayer: ActorRef, settings:CampaignSettings) extends Actor with ActorLogging{

  var state = CampaignActor.State()

  protected val system: ActorSystem = context.system
  protected implicit val executor : ExecutionContextExecutor = context.dispatcher

  override def receive: Receive = {
    case CampaignActor.Initialize =>
      datalayer ! DataLayerActor.TopNRequest(settings.category,settings.memberSettings.num,settings.memberSettings.best,DataLayerActor.Subscribers)
      datalayer ! DataLayerActor.TopNRequest(settings.category,settings.contentSettings.num,settings.contentSettings.best,DataLayerActor.Products)
      mailchimp ! MailchimpActor.CreateList(settings.list)
    case CampaignActor.Idle =>
      log.info(s"State updated: $state")
      if(state.members.nonEmpty && state.content.nonEmpty && state.list.nonEmpty ) {
        if(state.campaign.isEmpty){
          mailchimp ! MailchimpActor.CreateCampaign(state.list.get.id,settings.campaign)
          mailchimp ! MailchimpActor.AddMembers(state.list.get.id, state.members.get)
        } else {
          if(state.hasContent && state.hasMember) {
            mailchimp ! MailchimpActor.SendCampaign(state.campaign.get.id)
          } else if (!state.hasContent) {
            mailchimp ! MailchimpActor.AddCampaignContent(state.campaign.get.id,state.content.get)
          }
        }
      }
    case subscribers: SubscriberAggregationsEmbedded =>
      val members = subscribers.embedded.subscribersAgg.map(s => Member(s.email, "subscribed"))
      log.info(s"Campaign members ready: $members")
      state=state.copy(members=Some(members))
      self ! CampaignActor.Idle
    case products: ProductAggregationsEmbedded =>
      val variableContent : String = products.embedded.productAggs.map(p=>settings.contentSettings.variableFormat.format(p.name)).mkString("\n")
      val htmlContent : String = settings.contentSettings.contentFormat.format(variableContent)
      log.info(s"Campaign content ready: $htmlContent")
      state=state.copy(content=Some(Content(htmlContent)))
      self ! CampaignActor.Idle
    case list: CampaignList =>
      log.info(s"Campaign list ready: $list")
      state=state.copy(list=Some(list))
      self ! CampaignActor.Idle
    case campaign: Campaign =>
      log.info(s"Campaign  ready: $campaign")
      state=state.copy(campaign=Some(campaign))
      self ! CampaignActor.Idle
    case event: MailchimpActor.AddedMembers =>
      log.info(s"Campaign members added!")
      state = state.copy(hasMember = true)
      self ! CampaignActor.Idle
    case event: MailchimpActor.AddedCampaignContent =>
      log.info(s"Campaign content added!")
      state = state.copy(hasContent = true)
      self ! CampaignActor.Idle
    case event: MailchimpActor.SentCampaign =>
      log.info("Success while sending campaign")
      context.stop(self)
    case _ =>
      log.error(s"Unsupported event from ${sender.path}!")
      context.stop(self)
  }
}
