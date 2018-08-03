package com.github.jw3.di

import akka.actor.ActorSystem
import akka.testkit.TestKit
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.{Matchers, WordSpecLike}

// issue #32
class LazyManualConfigSpec
    extends TestKit(ActorSystem("lazy"))
    with WordSpecLike
    with Matchers {

  "late modules" should {
    "be accepted" in {
      InjectExt(system).addModules(LateModule1)
      InjectExt(system).addModules(LateModule2)
    }

    "be injected" in {
      val foo: String = inject[String] annotated "foo"
      foo shouldBe "bar"

      val baz: String = inject[String] annotated "baz"
      baz shouldBe "bash"
    }

    "be rejected once inited" in {
      intercept[UnsupportedOperationException] {
        InjectExt(system).addModules(LateModule3)
      }
    }
  }
}

object LateModule1 extends ScalaModule {
  override def configure(): Unit = {
    bind[String].annotatedWithName("foo").toInstance("bar")
  }
}

object LateModule2 extends ScalaModule {
  override def configure(): Unit = {
    bind[String].annotatedWithName("baz").toInstance("bash")
  }
}

object LateModule3 extends ScalaModule {
  override def configure(): Unit = ()
}
