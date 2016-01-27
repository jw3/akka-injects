package com.rxthings.di

import java.util.UUID

import akka.actor.ActorRef
import com.google.inject.name.Names
import com.rxthings.di.AnnotatedInjectionSpec.{IBadActor, _}
import com.rxthings.di.test.{InjectSpec, NopActor}
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers

import scala.util.Random

/**
 * validate the annotation support
 */
class AnnotatedInjectionSpec extends InjectSpec with Matchers {

    "annotated injection" should {
        injectTest("throw when actor not annotated", AnnoBind) { implicit sys =>
            intercept[Exception] {inject[IBadActor].required}
        }

        injectTest("inject when actor is annotated", AnnoBind) { implicit sys =>
            injectActor[IBadActor].annotated(anno).required shouldBe a[ActorRef]
        }

        injectTest("throw when Int not annotated", AnnoBind) { implicit sys =>
            intercept[Exception] {inject[Int].required}
        }

        injectTest("inject when Int is annotated", AnnoBind) { implicit sys =>
            inject[Int].annotated(anno).required shouldBe intVal
        }

        injectTest("throw when String not annotated", AnnoBind) { implicit sys =>
            intercept[Exception] {inject[String].required}
        }

        injectTest("inject when String is annotated", AnnoBind) { implicit sys =>
            inject[String].annotated(anno).required shouldBe stringVal
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
