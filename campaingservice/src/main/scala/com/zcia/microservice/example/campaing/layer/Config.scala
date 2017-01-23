package com.zcia.microservice.example.campaing.layer

import com.typesafe.config.ConfigFactory


trait Config {
  private val config  = ConfigFactory.load()
  protected val httpConfig = config.getConfig("http")
  protected val datalayerConfig = config.getConfig("datalayer")

}
