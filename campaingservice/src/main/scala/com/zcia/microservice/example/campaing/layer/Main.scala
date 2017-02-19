package com.zcia.microservice.example.campaing.layer

import akka.actor.Props

object Main extends App with CampaignConfig {
  val mailchimp = System.system.actorOf(Props[MailchimpActor],"mailchimp")
  val datalayer = System.system.actorOf(Props[DataLayerActor],"datalayer")
  val campaign = System.system.actorOf(Props(new CampaignActor(mailchimp,datalayer,settings)),"campaign")

  campaign ! CampaignActor.Initialize
}