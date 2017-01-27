package com.zcia.microservice.example.campaing.layer

/**
  * Created by ejoszum on 24/01/2017.
  */


object MailchimpDomain {
  case class Member(email_address: String, status: String)
  case class Contact(company: String, address1: String, city: String, state: String, zip: String, country: String)
  case class CampaignDefaults(from_name: String, from_email: String, subject: String = "", language: String = "en")
  case class List(id: Option[String], name: String, contact: Contact, permission_reminder: String, campaign_defaults: CampaignDefaults, email_type_option: Boolean = true)
  case class Recipient(list_id: String)
  case class Settings(subject_line: String, reply_to: String, from_name: String)
  case class Campaign(id: String, recipients: Recipient, typE: String, settings: Settings)
  case class Content(html: String)
}
