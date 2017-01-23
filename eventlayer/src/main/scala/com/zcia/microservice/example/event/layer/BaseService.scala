package com.zcia.microservice.example.event.layer

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport


trait BaseService extends BaseComponent with Protocol with SprayJsonSupport with Config
