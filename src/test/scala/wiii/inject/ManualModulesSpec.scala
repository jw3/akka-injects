package wiii.inject

import java.util.UUID

import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers
import wiii.inject.ManualModulesSpec.{IntM, StringM}
import wiii.inject.test.InjectSpec

import scala.util.Random

class ManualModulesSpec extends InjectSpec with Matchers {
    "single injections" should {
        injectTest("provide String", Seq(StringM)) { implicit sys =>
            injector.instance[String] shouldBe StringM.value
        }
        injectTest("provide Int", Seq(IntM)) { implicit sys =>
            injector.instance[Int] shouldBe IntM.value
        }
    }
    "composite injections" should {
        injectTest("provide Int,String", Seq(StringM, IntM)) { implicit sys =>
            injector.instance[String] shouldBe StringM.value
            injector.instance[Int] shouldBe IntM.value
        }
    }
}

object ManualModulesSpec {
    object StringM extends ScalaModule {
        val value = UUID.randomUUID.toString
        def configure(): Unit = bind[String].toInstance(value)
    }
    object IntM extends ScalaModule {
        val value = Random.nextInt()
        def configure(): Unit = bind[Int].toInstance(value)
    }
}
