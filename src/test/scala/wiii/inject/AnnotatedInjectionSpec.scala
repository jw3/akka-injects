package wiii.inject

import java.util.UUID

import akka.actor.ActorRef
import com.google.inject.name.Names
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers
import wiii.inject.AnnotatedInjectionSpec._
import wiii.inject.test.{InjectSpec, NopActor}

import scala.util.Random

/**
 * validate the annotation support
 */
class AnnotatedInjectionSpec extends InjectSpec with Matchers {

    "annotated injection" should {
        injectTest("throw when actor not annotated", Seq(AnnoBind)) { implicit sys =>
            intercept[Exception] {InjectActor[IBadActor].build}
        }

        injectTest("inject when actor is annotated", Seq(AnnoBind)) { implicit sys =>
            Inject [IBadActor].annotated(anno).build shouldBe a[ActorRef]
        }

        injectTest("throw when Int not annotated", Seq(AnnoBind)) { implicit sys =>
            intercept[Exception] {Inject[Int].build}
        }

        injectTest("inject when Int is annotated", Seq(AnnoBind)) { implicit sys =>
            Inject[Int].annotated(anno).build shouldBe intVal
        }

        injectTest("throw when String not annotated", Seq(AnnoBind)) { implicit sys =>
            intercept[Exception] {Inject[String].build}
        }

        injectTest("inject when String is annotated", Seq(AnnoBind)) { implicit sys =>
            Inject[String].annotated(anno).build shouldBe stringVal
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
