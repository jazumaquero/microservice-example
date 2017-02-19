package com.zcia.microservice.example.campaing.layer


case class MemberSettings(num: Integer, best: Boolean)
case class ContentSettings(num: Integer, best: Boolean , variableFormat : String, contentFormat : String)
case class CampaignSettings(list: CampaignList, campaign: Campaign, memberSettings: MemberSettings, contentSettings: ContentSettings, category: String)

trait CampaignProtocol extends Protocol {
  //implicit val memberSettingsFormat = jsonFormat2(MemberSettings)
  //implicit val contentSettingsFormat = jsonFormat4(ContentSettings)
  //implicit val campaignSettingsFormat = jsonFormat5(CampaignSettings)
}
