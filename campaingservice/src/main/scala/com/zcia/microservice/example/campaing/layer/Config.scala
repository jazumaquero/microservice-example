package com.zcia.microservice.example.campaing.layer

import com.typesafe.config.ConfigFactory


trait Config {
  protected val config  = ConfigFactory.load()
  protected val httpConfig = config.getConfig("http")
  protected val serviceConfig = config.getConfig("service")
}

trait DataLayerConfig extends Config {
  protected val datalayerConfig = config.getConfig("datalayer")
  val host : String= datalayerConfig.getString("host")
  val port : Integer = datalayerConfig.getInt("port")
}

trait MailchimpConfig extends Config {
  protected val mailchimpConfig = config.getConfig("mailchimp")
  val host: String = mailchimpConfig.getString("host")
  val version : String = mailchimpConfig.getString("version")
  val apiKey : String = mailchimpConfig.getString("apikey")
}

trait CampaignConfig extends Config {
  protected val contactConfig = serviceConfig.getConfig("contact")
  protected val campaignConfig=serviceConfig.getConfig("campaign")
  protected val campaignDefaultsConfig=campaignConfig.getConfig("defaults")
  protected val campaignSettings=campaignConfig.getConfig("settings")

  val configuredList: CampaignList =  {
    val contact = Contact(contactConfig.getString("company"),
      contactConfig.getString("address1"),
      contactConfig.getString("city"),
      contactConfig.getString("state"),
      contactConfig.getString("zip"),
      contactConfig.getString("country")
    )
    val defaults = CampaignDefaults(campaignDefaultsConfig.getString("from_name"),
      campaignDefaultsConfig.getString("from_email"),
      campaignDefaultsConfig.getString("subject"),
      campaignDefaultsConfig.getString("language")
    )
    CampaignList("foo", campaignConfig.getString("name"), contact, campaignConfig.getString("permission_reminder"), defaults )
  }
  val configuredCampaing: Campaign = {
    val settings = Settings(subject_line=campaignSettings.getString("subject_line"),
      title=campaignSettings.getString("title"),
      reply_to=campaignSettings.getString("reply_to"),
      from_name=campaignSettings.getString("from_name")
    )
    Campaign("", Recipient(""), campaignConfig.getString("type"), settings)
  }

  val memberSettings = MemberSettings(serviceConfig.getInt("subscribers.n") ,    serviceConfig.getString("subscribers.type").toLowerCase.equals("best")  )

  val contentSettings = ContentSettings(
    serviceConfig.getInt("products.n") ,
    serviceConfig.getString("products.type").toLowerCase.equals("best"),
    campaignConfig.getString("content.format.variable"),
    campaignConfig.getString("content.format.fixed"))

  val settings = CampaignSettings(configuredList, configuredCampaing, memberSettings, contentSettings,serviceConfig.getString("category"))
}