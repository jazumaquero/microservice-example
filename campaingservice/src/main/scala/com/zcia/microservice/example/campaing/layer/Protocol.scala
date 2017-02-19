package com.zcia.microservice.example.campaing.layer

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol

trait Protocol extends DefaultJsonProtocol with SprayJsonSupport
