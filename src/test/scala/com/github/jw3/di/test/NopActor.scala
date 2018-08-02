package com.github.jw3.di.test

import akka.actor.Actor

/**
 * no op actor
 */
trait NopActor extends Actor {
  def receive: Receive = {
    case _ =>
  }
}
