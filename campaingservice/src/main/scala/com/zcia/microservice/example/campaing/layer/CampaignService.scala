package com.zcia.microservice.example.campaing.layer

import akka.actor.Actor

class CampaignService extends Actor with CampaignServiceFSM {
  protected implicit def materializer = System.materializer
  protected implicit def system = System.system
  override protected implicit val executor = system.dispatcher
}
