package com.zcia.microservice.example.event.layer

import akka.event.LoggingAdapter

import scala.concurrent.ExecutionContext

/**
  * Created by zuma on 21/01/17.
  */
trait BaseComponent {
  protected implicit def log: LoggingAdapter
  protected implicit def executor: ExecutionContext
}

