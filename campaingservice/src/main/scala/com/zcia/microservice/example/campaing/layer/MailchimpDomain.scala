package com.zcia.microservice.example.campaing.layer

/**
  * Created by ejoszum on 24/01/2017.
  */
case class ListContact(company: String, address1: String, address2: String, city: String, state: String, zip: String, country: String, phone: String)

case class ListCampaingDefaults(from_name: String, from_email: String, subject: String, language: String)

case class List(name: String, contact: ListContact, permission_reminder: String, use_archive_bar: String, campaign_defaults: ListCampaingDefaults, notify_on_subscribe: String, notify_on_unsubscribe: String, email_type_option: Boolean, visibility: String)

case class Location(latitude: Number, longitud: Number)

case class Member(email_address: String, email_type: String, status: String, merge_fields: Object, interests: Object, language: String, vip: Boolean, location: Location, ip_signup: String, timestamp_signup: String, ip_opt: String, timestamp_opt: String)

case class ListMembers(members: Seq[Member], update_existing: Boolean)