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
      intercept[UnsupportedOperationException] {inject[IBadActor].required}
    }

    injectTest("throw when actor not annotated correctly", AnnoBind) { implicit sys =>
      intercept[IllegalStateException] {injectActor[IBadActor].annotated(UUID.randomUUID.toString).required}
    }

    injectTest("throw when unannotated actor is injected with annotation", AnnoBind) { implicit sys =>
      intercept[IllegalStateException] {injectActor[IUnannotatedActor].annotated(UUID.randomUUID.toString).required}
    }

    injectTest("handle mixed annotation states on actors", AnnoBind) { implicit sys =>
      injectActor[ISomeUnSomeAn].annotated(anno).required shouldBe a[ActorRef]
      injectActor[ISomeUnSomeAn].required shouldBe a[ActorRef]
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

    injectTest("throw when unannotated Double is injected with annotation", AnnoBind) { implicit sys =>
      intercept[IllegalStateException] {inject[Double].annotated(UUID.randomUUID.toString).required}
    }

    injectTest("handle mixed annotation states", AnnoBind) { implicit sys =>
      inject[Long].annotated(anno).required shouldBe annotatedLongVal
      inject[Long].required shouldBe unannotatedLongVal
    }

    injectTest("throw descriptive exception", AnnoBind) { implicit sys =>
      val name = "__doesnt_exist__"
      intercept[IllegalStateException] {
        inject[String].annotated(name).required
      }.getMessage should include(name)
    }
  }
}

object AnnotatedInjectionSpec {
  trait IActor extends NopActor
  trait IBadActor extends IActor
  trait IUnannotatedActor extends IActor
  trait ISomeUnSomeAn extends IActor
  class NickCage extends IBadActor
  class DannyDevito extends IUnannotatedActor
  class Un extends ISomeUnSomeAn
  class An extends ISomeUnSomeAn

  val anno = UUID.randomUUID.toString
  val stringVal = UUID.randomUUID.toString
  val intVal = Random.nextInt()
  val unannotatedDoubleVal = 2.2D

  val annotatedLongVal = 1L
  val unannotatedLongVal = 2L

  object AnnoBind extends ScalaModule {
    def configure(): Unit = {
      bind[IUnannotatedActor].to[DannyDevito]
      bind[IBadActor].annotatedWith(Names.named(anno)).to[NickCage]

      bind[String].annotatedWith(Names.named(anno)).toInstance(stringVal)
      bind[Int].annotatedWith(Names.named(anno)).toInstance(intVal)
      bind[Double].toInstance(unannotatedDoubleVal)

      bind[Long].toInstance(unannotatedLongVal)
      bind[Long].annotatedWith(Names.named(anno)).toInstance(annotatedLongVal)

      bind[ISomeUnSomeAn].to[Un]
      bind[ISomeUnSomeAn].annotatedWith(Names.named(anno)).to[An]
    }
  }
}
