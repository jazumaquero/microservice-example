package com.zcia.microservice.example.event.layer

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport


/**
  * Created by zuma on 21/01/17.
  */
trait BaseService extends BaseComponent with Protocol with SprayJsonSupport with Config
