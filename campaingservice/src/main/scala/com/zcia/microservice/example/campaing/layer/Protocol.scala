package com.zcia.microservice.example.campaing.layer


import spray.json.DefaultJsonProtocol


// Mailchimp domain classes
case class Member(email_address: String, status: String)
case class Members(members: Seq[Member])
case class Contact(company: String, address1: String, city: String, state: String, zip: String, country: String)
case class CampaignDefaults(from_name: String, from_email: String, subject: String = "", language: String = "en")
case class CampaignList(id: String, name: String,contact: Contact, permission_reminder: String, campaign_defaults: CampaignDefaults,email_type_option: Boolean=true)
case class Recipient(list_id: String)
case class Settings(subject_line: String, reply_to: String, from_name: String)
case class Campaign(id: String, recipients: Recipient, campaignType: String, settings: Settings)
case class Content(html: String)

// Datalayer domain classes
// TODO add href to allow a more useful campaign content
case class SubscriberAggregations(name: String,email: String,category: String,num: Long)
case class ProductAggregations(name: String,category: String,num: Long)

trait Protocol extends DefaultJsonProtocol {
  // Mailchimp Domain protocols
  implicit val memberFormat = jsonFormat(Member,"email_address", "status")
  implicit val membersFormat = jsonFormat(Members,"members")
  implicit val contactFormat = jsonFormat(Contact, "company" ,"address1", "city" ,"state", "zip" ,"country")
  implicit val campaignDefaultsFormat = jsonFormat(CampaignDefaults, "from_name", "from_email", "subject", "language")
  implicit val campaignListFormat = jsonFormat(CampaignList, "id", "name", "contact","permission_reminder", "campaign_defaults", "email_type_option")
  implicit val recipientFormat = jsonFormat(Recipient, "list_id")
  implicit val settingsFormat = jsonFormat(Settings, "subject_line","reply_to","from_name")
  implicit val campaignFormat = jsonFormat(Campaign,"id", "recipients", "type", "settings")
  implicit val contentFormat = jsonFormat(Content, "html")

  // DataLayer Domain protocols
  implicit val subscriberAggregationFormat = jsonFormat(SubscriberAggregations,"subscriberName","subscriberEmail","categoryName","num")
  implicit val productAggregationFormat = jsonFormat(ProductAggregations,"productName","categoryName","num")
}
