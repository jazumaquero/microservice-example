package com.zcia.microservice.example.campaing.layer

import akka.http.scaladsl.unmarshalling.Unmarshal

import scala.util.{Failure, Success}

trait CampaingService extends BaseService with DatalayerClient with MailchimpClient{

  protected val contactConfig=mailchimpConfig.getConfig("contact")
  protected val campaingConfig=mailchimpConfig.getConfig("campaign")
  protected val campaingDefaultsConfig=campaingConfig.getConfig("defaults")
  protected val campaingSettings=campaingConfig.getConfig("settings")

  protected val contact = new Contact(contactConfig.getString("company"),
    contactConfig.getString("address1"),
    contactConfig.getString("city"),
    contactConfig.getString("state"),
    contactConfig.getString("zip"),
    contactConfig.getString("country")
  )

  protected val defaults = new CampaignDefaults(campaingDefaultsConfig.getString("from_name"),
    campaingDefaultsConfig.getString("from_email"),
    campaingDefaultsConfig.getString("subject"),
    campaingDefaultsConfig.getString("language")
  )

  protected val settings = new Settings(subject_line=campaingSettings.getString("subject_line"),
    reply_to=campaingSettings.getString("reply_to"),
    from_name=campaingSettings.getString("from_name")
  )

  protected val list = new CampaignList("foo", campaingConfig.getString("name"), contact, campaingConfig.getString("permission_reminder"), defaults )

  protected def processCampaing() : Unit = {
    // Create some campaing list
    createList(list) onComplete {
      case Success(response) => {
        Unmarshal(response.entity).to[CampaignList] onComplete { list =>
          log.info(s"Created campaign list: $list")
        }
      }
      case Failure(ex) => log.error(s"Fail while creating campaing list: ${ex.getMessage}")
    }
  }
}
