package com.zcia.microservice.example.campaing.layer

import akka.actor.Props


object Main extends App with Config {
  protected val contactConfig = mailchimpConfig.getConfig("contact")
  protected val campaingConfig=mailchimpConfig.getConfig("campaign")
  protected val campaingDefaultsConfig=campaingConfig.getConfig("defaults")
  protected val campaingSettings=campaingConfig.getConfig("settings")

  val configuredList: CampaignList =  {
    val contact = new Contact(contactConfig.getString("company"),
      contactConfig.getString("address1"),
      contactConfig.getString("city"),
      contactConfig.getString("state"),
      contactConfig.getString("zip"),
      contactConfig.getString("country")
    )
    val defaults = new CampaignDefaults(campaingDefaultsConfig.getString("from_name"),
      campaingDefaultsConfig.getString("from_email"),
      campaingDefaultsConfig.getString("subject"),
      campaingDefaultsConfig.getString("language")
    )
    new CampaignList("foo", campaingConfig.getString("name"), contact, campaingConfig.getString("permission_reminder"), defaults )
  }

  val configuredMembers: Seq[Member] = Seq()

  val configuredCampaing: Campaign = {
    val settings = new Settings(subject_line=campaingSettings.getString("subject_line"),
      reply_to=campaingSettings.getString("reply_to"),
      from_name=campaingSettings.getString("from_name")
    )
    new Campaign("", new Recipient(""), campaingConfig.getString("type"), settings)
  }

  val configuredContent = new Content("<p>Este correo se autodestruirá en 30 segundos....</p>")

  val campaignService = System.system.actorOf(Props(new CampaignService(configuredList,configuredMembers,configuredCampaing,configuredContent)))

  campaignService ! new CampaignService.Initialize()
}