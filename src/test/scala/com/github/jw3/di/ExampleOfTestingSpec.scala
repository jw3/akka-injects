package com.github.jw3.di

import akka.testkit.TestActorRef
import com.github.jw3.di.test.InjectSpec
import com.github.jw3.di.ExampleOfTestingSpec.M1
import com.github.jw3.di.test.NopActor
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
  import context._
  val name: String = inject[String] annotated "actor.name"
}

object ExampleOfTestingSpec {

  object M1 extends ScalaModule {
    val actorName = "bob"
    override def configure(): Unit = {
      bind[String].annotatedWithName("actor.name").toInstance(actorName)
    }
  }
}
