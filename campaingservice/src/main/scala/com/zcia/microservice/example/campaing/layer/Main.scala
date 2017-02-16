package com.zcia.microservice.example.campaing.layer

import akka.actor.Props

// TODO transform into a service that accepts the campaign service settings as DTO
object Main extends App with Config {
  protected val contactConfig = serviceConfig.getConfig("contact")
  protected val campaignConfig=serviceConfig.getConfig("campaign")
  protected val campaignDefaultsConfig=campaignConfig.getConfig("defaults")
  protected val campaignSettings=campaignConfig.getConfig("settings")

  val configuredList: CampaignList =  {
    val contact = new Contact(contactConfig.getString("company"),
      contactConfig.getString("address1"),
      contactConfig.getString("city"),
      contactConfig.getString("state"),
      contactConfig.getString("zip"),
      contactConfig.getString("country")
    )
    val defaults = new CampaignDefaults(campaignDefaultsConfig.getString("from_name"),
      campaignDefaultsConfig.getString("from_email"),
      campaignDefaultsConfig.getString("subject"),
      campaignDefaultsConfig.getString("language")
    )
    new CampaignList("foo", campaignConfig.getString("name"), contact, campaignConfig.getString("permission_reminder"), defaults )
  }
  val configuredCampaing: Campaign = {
    val settings = new Settings(subject_line=campaignSettings.getString("subject_line"),
      reply_to=campaignSettings.getString("reply_to"),
      from_name=campaignSettings.getString("from_name")
    )
    new Campaign("", new Recipient(""), campaignConfig.getString("type"), settings)
  }

  val memberSettings = new MemberSettings(serviceConfig.getInt("subscribers.n") ,    serviceConfig.getString("subscribers.type").toLowerCase.equals("best")  )

  val contentSettings = new ContentSettings(
    serviceConfig.getInt("products.n") ,
    serviceConfig.getString("products.type").toLowerCase.equals("best"),
    campaignConfig.getString("content.format.variable"),
    campaignConfig.getString("content.format.fixed"))
  val settings = new CampaignSettings(configuredList, configuredCampaing, memberSettings, contentSettings,serviceConfig.getString("category"))

 // val campaignService = System.system.actorOf(Props(new CampaignService(settings)))
  //campaignService ! new CampaignService.Initialize()
  val mailchimp = System.system.actorOf(Props[MailchimpActor],"mailchimp")
  val datalayer = System.system.actorOf(Props[DataLayerActor],"datalayer")
  val campaign = System.system.actorOf(Props(new CampaignActor(mailchimp,datalayer,settings)),"campaign")

  campaign ! CampaignActor.Initialize
}