package com.zcia.microservice.example.campaing.layer

import akka.actor.Props


object Main extends App {
  val campaignService = System.system.actorOf(Props(new CampaignService()))
  campaignService ! Initialize
}