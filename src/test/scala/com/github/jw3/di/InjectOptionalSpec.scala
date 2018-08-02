package com.github.jw3.di

import akka.actor.{Actor, ActorRef}
import com.github.jw3.di.InjectOptionalSpec.{BindsStuff, IDoExist, IDontExist}
import com.github.jw3.di.test.{InjectSpec, NopActor}
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers

/**
 * validate the ability to do injections optionally
 */
class InjectOptionalSpec extends InjectSpec with Matchers {

  "optional injection" should {
    injectTest("inject none for actors when no bindings are present") { implicit sys =>
      injectActor[Actor].optional should not be defined
    }

    injectTest("inject none for boxed when no bindings are present") { implicit sys =>
      inject[Int].optional should not be defined
    }

    injectTest("inject none for trait when no bindings are present") { implicit sys =>
      inject[IDontExist].optional should not be defined
    }

    injectTest("inject Some for actors when no bindings are present", Seq(BindsStuff)) { implicit sys =>
      injectActor[Actor].optional shouldBe defined
    }

    injectTest("inject Some for boxed when no bindings are present", Seq(BindsStuff)) { implicit sys =>
      inject[Int].optional shouldBe defined
    }

    injectTest("inject Some for trait when no bindings are present", Seq(BindsStuff)) { implicit sys =>
      inject[IDoExist].optional shouldBe defined
    }
  }

  "optional injection should work implicitly" should {
    injectTest("inject none for actors when no bindings are present") { implicit sys =>
      val opt: Option[ActorRef] = injectActor[Actor]
      opt should not be defined
    }

    injectTest("inject none for boxed when no bindings are present") { implicit sys =>
      val opt: Option[Int] = inject[Int]
      opt should not be defined
    }

    injectTest("inject none for trait when no bindings are present") { implicit sys =>
      val opt: Option[IDontExist] = inject[IDontExist]
      opt should not be defined
    }

    injectTest("inject Some for actors when no bindings are present", Seq(BindsStuff)) { implicit sys =>
      val opt: Option[ActorRef] = injectActor[Actor]
      opt shouldBe defined
    }

    injectTest("inject Some for boxed when no bindings are present", Seq(BindsStuff)) { implicit sys =>
      val opt: Option[Int] = inject[Int]
      opt shouldBe defined
    }

    injectTest("inject Some for trait when no bindings are present", Seq(BindsStuff)) { implicit sys =>
      val opt: Option[IDoExist] = inject[IDoExist]
      opt shouldBe defined
    }
  }
}

object InjectOptionalSpec {
  trait IDontExist
  trait IDoExist
  class DoesExists extends IDoExist
  class AnActor extends NopActor

  object BindsStuff extends ScalaModule {
    override def configure(): Unit = {
      bind[IDoExist].to[DoesExists]
      bind[String].toInstance("somestring")
      bind[Int].toInstance(9999)
      bind[Actor].to[AnActor]
    }
  }
}
