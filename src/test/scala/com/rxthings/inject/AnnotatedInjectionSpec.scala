package com.rxthings.inject

import java.util.UUID

import akka.actor.ActorRef
import com.google.inject.name.Names
import com.rxthings.inject.AnnotatedInjectionSpec.{IBadActor, _}
import com.rxthings.inject.test.{InjectSpec, NopActor}
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers

import scala.util.Random

/**
 * validate the annotation support
 */
class AnnotatedInjectionSpec extends InjectSpec with Matchers {

    "annotated injection" should {
        injectTest("throw when actor not annotated", AnnoBind) { implicit sys =>
            intercept[Exception] {InjectActor[IBadActor].required}
        }

        injectTest("inject when actor is annotated", AnnoBind) { implicit sys =>
            InjectActor[IBadActor].annotated(anno).required shouldBe a[ActorRef]
        }

        injectTest("throw when Int not annotated", AnnoBind) { implicit sys =>
            intercept[Exception] {Inject[Int].required}
        }

        injectTest("inject when Int is annotated", AnnoBind) { implicit sys =>
            Inject[Int].annotated(anno).required shouldBe intVal
        }

        injectTest("throw when String not annotated", AnnoBind) { implicit sys =>
            intercept[Exception] {Inject[String].required}
        }

        injectTest("inject when String is annotated", AnnoBind) { implicit sys =>
            Inject[String].annotated(anno).required shouldBe stringVal
        }
    }
}

object AnnotatedInjectionSpec {
    trait IBadActor extends NopActor
    class NickCage extends IBadActor

    val anno = UUID.randomUUID.toString
    val stringVal = UUID.randomUUID.toString
    val intVal = Random.nextInt()

    object AnnoBind extends ScalaModule {
        def configure(): Unit = {
            bind[IBadActor].annotatedWith(Names.named(anno)).to[NickCage]
            bind[String].annotatedWith(Names.named(anno)).toInstance(stringVal)
            bind[Int].annotatedWith(Names.named(anno)).toInstance(intVal)
        }
    }
}
