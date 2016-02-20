package com.rxthings.di

import akka.actor.ActorSystem
import com.rxthings.di.test.InjectSpec
import org.scalatest.Matchers
import net.codingwell.scalaguice.InjectorExtensions._


/**
 * validate things that are configured as injected by default
 * - ActorSystem
 */
class DefaultInjectionsSpec extends InjectSpec with Matchers {

  "default injection" should {
    injectTest("include singleton ActorSystem") { implicit sys =>
      val a = injector.instance[ActorSystem]
      val b = injector.instance[ActorSystem]
      a shouldBe theSameInstanceAs(b)
    }
  }
}
