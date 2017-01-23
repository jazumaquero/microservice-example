package com.zcia.microservice.example.event.layer

import com.typesafe.config.ConfigFactory

/**
  * Created by zuma on 21/01/17.
  */
trait Config {
  private val config  = ConfigFactory.load()
  protected val httpConfig = config.getConfig("http")
  protected val datalayerConfig = config.getConfig("datalayer")

}
