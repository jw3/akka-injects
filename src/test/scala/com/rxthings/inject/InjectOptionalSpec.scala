package com.rxthings.inject

import akka.actor.{Actor, ActorRef}
import com.rxthings.inject.InjectOptionalSpec.{BindsStuff, IDoExist, IDontExist}
import com.rxthings.inject.test.{InjectSpec, NopActor}
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers

/**
 * validate the ability to do injections optionally
 */
class InjectOptionalSpec extends InjectSpec with Matchers {

    "optional injection" should {
        injectTest("inject none for actors when no bindings are present") { implicit sys =>
            InjectActor[Actor].optional should not be defined
        }

        injectTest("inject none for boxed when no bindings are present") { implicit sys =>
            Inject[Int].optional should not be defined
        }

        injectTest("inject none for trait when no bindings are present") { implicit sys =>
            Inject[IDontExist].optional should not be defined
        }

        injectTest("inject Some for actors when no bindings are present", Seq(BindsStuff)) { implicit sys =>
            InjectActor[Actor].optional shouldBe defined
        }

        injectTest("inject Some for boxed when no bindings are present", Seq(BindsStuff)) { implicit sys =>
            Inject[Int].optional shouldBe defined
        }

        injectTest("inject Some for trait when no bindings are present", Seq(BindsStuff)) { implicit sys =>
            Inject[IDoExist].optional shouldBe defined
        }
    }

    "optional injection should work implicitly" should {
        injectTest("inject none for actors when no bindings are present") { implicit sys =>
            val opt: Option[ActorRef] = InjectActor[Actor]
            opt should not be defined
        }

        injectTest("inject none for boxed when no bindings are present") { implicit sys =>
            val opt: Option[Int] = Inject[Int]
            opt should not be defined
        }

        injectTest("inject none for trait when no bindings are present") { implicit sys =>
            val opt: Option[IDontExist] = Inject[IDontExist]
            opt should not be defined
        }

        injectTest("inject Some for actors when no bindings are present", Seq(BindsStuff)) { implicit sys =>
            val opt: Option[ActorRef] = InjectActor[Actor]
            opt shouldBe defined
        }

        injectTest("inject Some for boxed when no bindings are present", Seq(BindsStuff)) { implicit sys =>
            val opt: Option[Int] = Inject[Int]
            opt shouldBe defined
        }

        injectTest("inject Some for trait when no bindings are present", Seq(BindsStuff)) { implicit sys =>
            val opt: Option[IDoExist] = Inject[IDoExist]
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
        def configure(): Unit = {
            bind[IDoExist].to[DoesExists]
            bind[String].toInstance("somestring")
            bind[Int].toInstance(9999)
            bind[Actor].to[AnActor]
        }
    }
}