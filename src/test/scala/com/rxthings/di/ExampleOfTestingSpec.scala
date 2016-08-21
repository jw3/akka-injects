package com.rxthings.di

import akka.testkit.TestActorRef
import com.rxthings.di.ExampleOfTestingSpec.M1
import com.rxthings.di.test.{InjectSpec, NopActor}
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers

/**
 *
 */
class ExampleOfTestingSpec extends InjectSpec with Matchers {
  "Using TestActorRef" should {
    injectTest("support injection", M1) { implicit sys =>
      val actor = TestActorRef[InjectedActor]
      actor.underlyingActor.name shouldBe M1.actorName
    }
  }
}

class InjectedActor extends NopActor {
  val name: String = inject[String] annotated "actor.name"
}

object ExampleOfTestingSpec {

  object M1 extends ScalaModule {
    val actorName = "bob"
    def configure(): Unit = {
      bind[String].annotatedWithName("actor.name").toInstance(actorName)
    }
  }
}
