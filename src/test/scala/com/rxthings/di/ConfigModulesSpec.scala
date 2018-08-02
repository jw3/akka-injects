package com.rxthings.di

import java.util.UUID

import com.rxthings.di.test.InjectSpec
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers

import scala.util.Random

class ConfigModulesSpec extends InjectSpec with Matchers {
  import ConfigModulesSpec._

  "single injections" should {
    injectTest("provide String", cfg = cfg(cfgMode, modCfg(smod))) { implicit sys =>
      injector.instance[String] shouldBe sval
    }
    injectTest("provide Int", cfg = cfg(cfgMode, modCfg(imod))) { implicit sys =>
      injector.instance[Int] shouldBe ival
    }
  }
  "composite injections" should {
    injectTest("provide Int,String", cfg = cfg(cfgMode, modCfg(smod, imod))) { implicit sys =>
      injector.instance[String] shouldBe sval
      injector.instance[Int] shouldBe ival
    }
  }
}

object ConfigModulesSpec {
  val smod = "com.rxthings.di.StringM"
  val imod = "com.rxthings.di.IntM"
  val sval = UUID.randomUUID.toString
  val ival = Random.nextInt
}

class StringM extends ScalaModule {
  override def configure(): Unit = bind[String].toInstance(ConfigModulesSpec.sval)
}
class IntM extends ScalaModule {
  override def configure(): Unit = bind[Int].toInstance(ConfigModulesSpec.ival)
}

