package com.rxthings.di

import java.util.UUID

import akka.actor.ActorRef
import akka.testkit.TestActorRef
import com.rxthings.di.test.{InjectSpec, NopActor}
import net.codingwell.scalaguice._
import org.scalatest.Matchers


class InjectActorSpec extends InjectSpec with Matchers {
    import InjectActorSpec._

    "actor injection" should {
        injectTest("should work with traits", SimpleModule) { implicit sys =>
            val aa: ActorRef = injectActor[IAnActor]
            aa should not be null
        }

        injectTest("should inject actor members", SimpleModule) { implicit sys =>
            val testref = TestActorRef[AnActorWithInjects]
            testref.underlyingActor.injected shouldBe "foo"
        }

        injectTest("descriptive exception", SimpleModule) { implicit sys =>
            val name = UUID.randomUUID.toString
            intercept[IllegalStateException] {
                injectActor[IAnActor].annotated(name).required
            }.getMessage should include(name)
        }
    }
}

object InjectActorSpec {
    trait IAnActor extends NopActor
    class AnActor extends IAnActor

    class AnActorWithInjects extends NopActor {
        val injected: String = inject[String]
    }

    object SimpleModule extends ScalaModule {
        def configure(): Unit = {
            bind[IAnActor].to[AnActor]
            bind[String].toInstance("foo")
        }
    }
}
