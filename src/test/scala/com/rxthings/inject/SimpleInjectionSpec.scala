package com.rxthings.inject

import java.util.UUID

import akka.actor.Actor
import com.rxthings.inject.SimpleInjectectionSpec._
import com.rxthings.inject.test.InjectSpec
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers

import scala.util.Random


class SimpleInjectionSpec extends InjectSpec with Matchers {

    "simple injection" should {
        injectTest("throw if trying to inject actor, regardless of binding presence") { implicit sys =>
            intercept[Exception] {Inject[Actor].required}
        }

        injectTest("throw for boxed when no bindings are present") { implicit sys =>
            intercept[Exception] {Inject[Int].required}
        }

        injectTest("throw for String when no bindings are present") { implicit sys =>
            intercept[Exception] {Inject[String].required}
        }

        injectTest("throw for trait when no bindings are present") { implicit sys =>
            intercept[Exception] {Inject[IDontExist].required}
        }

        injectTest("inject boxed when bindings are present", Seq(SimpleBindings)) { implicit sys =>
            Inject[Int].required shouldBe intVal
        }

        injectTest("inject String when bindings are present", Seq(SimpleBindings)) { implicit sys =>
            Inject[String].required shouldBe stringVal
        }

        injectTest("inject trait when bindings are present", Seq(SimpleBindings)) { implicit sys =>
            Inject[IDoExist].required shouldBe DoesExists
        }
    }
}

object SimpleInjectectionSpec {
    trait IDontExist
    trait IDoExist
    object DoesExists extends IDoExist

    val stringVal = UUID.randomUUID.toString
    val intVal = Random.nextInt()

    object SimpleBindings extends ScalaModule {
        def configure(): Unit = {
            bind[IDoExist].toInstance(DoesExists)
            bind[String].toInstance(stringVal)
            bind[Int].toInstance(intVal)
        }
    }
}
