package com.zcia.microservice.example.campaing.layer

import akka.actor.{Actor, FSM}

sealed trait State
case object Initialize extends State
case object CreateList extends State
case object AddMembers extends State
case object CreateCampaign extends State
case object AddCampaignContent extends State
case object SendCampaing extends State
case object Error extends State
case object End extends State

trait CampaignServiceFSM extends Actor with FSM[State, Any] with MailchimpClient {

  startWith(CreateList, getConfiguredList())

  when(Initialize)  {
    case Event(Initialize,_) => {
      goto(CreateList).using(getConfiguredList())
    }
  }

  when(CreateList) {
    case Event(Initialize, list: CampaignList) => {
      createList2(list) match {
        case Right(l) => goto(AddMembers).using(l)
        case Left(ex) => goto(Error).using("Fail while creating campaign list!" -> ex)
      }
    }
  }

  when(AddMembers) {
    case Event(CreateList, list: CampaignList) => {
      addMembersToList2(list, getMembers()) match {
        case Right(_) => goto(CreateCampaign).using(list)
        case Left(ex) => goto(Error).using("Fail while creating adding members to list!" -> ex)
      }
    }
  }

  when(CreateCampaign) {
    case Event(CreateList, list: CampaignList) => {
      createCampaign2(getConfiguredCampaing(list)) match {
        case Right(campaign) => goto(AddCampaignContent).using(campaign)
        case Left(ex) => goto(Error).using("Fail while creating adding members to list!" -> ex)
      }
    }
  }

  when(AddCampaignContent) {
    case Event(CreateCampaign, campaign: Campaign) => {
      createCampaignContent2(campaign, getConfiguredContent()) match {
        case Right(campaign) => goto(SendCampaing).using(campaign)
        case Left(ex) => goto(Error).using("Fail while adding content to campaign!" -> ex)
      }
    }
  }

  when(SendCampaing) {
    case Event(AddCampaignContent, campaign: Campaign) => {
      sendCampaing2(campaign) match {
        case Right(campaign) => goto(End).using(campaign)
        case Left(ex) => goto(Error).using("Fail while sending campaign!" -> ex)
      }
    }
  }

  when(Error) {
    case Event(_, cause: (String, Exception)) => {
      log.error(s"${cause._1}")
      log.error(s"Aborting due to following exception: ${cause._2.getMessage}")
      stop()
    }
  }

  when(End) {
    case Event(_, _) => stop()
  }

  initialize()

}
