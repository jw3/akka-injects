package com.rxthings.inject

import akka.actor.ActorRef
import com.rxthings.inject.test.{InjectSpec, NopActor}
import net.codingwell.scalaguice._
import org.scalatest.Matchers


class InjectActorSpec extends InjectSpec with Matchers {
    import InjectActorSpec._

    "actor injection" should {
        injectTest("should work with traits", SimpleModule) { implicit sys =>
            val aa: ActorRef = InjectActor[IAnActor]
            aa should not be null
        }
    }

}

object InjectActorSpec {
    trait IAnActor extends NopActor
    class AnActor extends IAnActor

    object SimpleModule extends ScalaModule {
        def configure(): Unit = bind[IAnActor].to[AnActor]
    }
}
