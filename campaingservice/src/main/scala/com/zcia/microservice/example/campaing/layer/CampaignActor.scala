package com.zcia.microservice.example.campaing.layer

import akka.actor.{Actor, ActorLogging, ActorRef}

object CampaignActor{
  case object Initialize
  case class State(members: Option[Seq[Member]]=None, content: Option[Content]=None, list: Option[CampaignList]=None , campaign: Option[Campaign]=None)
}

class CampaignActor(mailchimp: ActorRef, datalayer: ActorRef, settings:CampaignSettings) extends Actor with ActorLogging{

  var state = new CampaignActor.State()

  override def receive: Receive = {
    case CampaignActor.Initialize => {
      datalayer ! new DataLayerActor.TopNRequest(settings.category,settings.memberSettings.num,settings.memberSettings.best,DataLayerActor.Subscribers)
    }
    case subscribers: Seq[SubscriberAggregation] => {
      val members = subscribers.map(s => new Member(s.email, "subscribed"))
      state=state.copy(members=Some(members))
      datalayer ! new DataLayerActor.TopNRequest(settings.category,settings.contentSettings.num,settings.contentSettings.best,DataLayerActor.Products)
    }
    case products: Seq[ProductAggregation] => {
      val variableContent : String = products.map(p=>settings.contentSettings.variableFormat.format(p.name)).mkString("\n")
      val htmlContent : String = settings.contentSettings.contentFormat.format(variableContent)
      state=state.copy(content=Some(new Content(htmlContent)))
      mailchimp ! new MailchimpActor.CreateList(settings.list)
    }
    case list: CampaignList => {
      state=state.copy(list=Some(list))
      mailchimp ! new MailchimpActor.AddMembers(state.list.get.id,state.members.get)
    }
    case event: MailchimpActor.AddedMembers => {
      mailchimp ! new MailchimpActor.CreateCampaign(event.listId,settings.campaign)
    }
    case campaign: Campaign => {
      state=state.copy(campaign=Some(campaign))
      mailchimp ! new MailchimpActor.AddCampaignContent(campaign.id,state.content.get)
    }
    case event: MailchimpActor.AddedCampaignContent => {
      mailchimp ! new MailchimpActor.SendCampaign(event.campaignId)
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
