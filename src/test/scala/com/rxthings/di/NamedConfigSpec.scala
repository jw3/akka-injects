package com.rxthings.di

import com.google.inject.Guice
import com.google.inject.name.Names
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import net.codingwell.scalaguice.InjectorExtensions._
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

/**
 * validate the binding of Config values by their key name
 */
class NamedConfigSpec extends WordSpec with Matchers {
  import NamedConfigSpec._

  "string bindings" should {
    "be bound by name" in {
      val (k, v) = rkey() -> rkey()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.instance[String](Names.named(k)) shouldBe v
    }

    "not be bound without name" in {
      val (k, v) = rkey() -> Random.nextBoolean()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.existingBinding[String] should not be defined
    }
  }

  "int bindings" should {
    "be bound by name" in {
      val (k, v) = rkey() -> Random.nextInt()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.instance[Int](Names.named(k)) shouldBe v
    }

    "not be bound without name" in {
      val (k, v) = rkey() -> Random.nextBoolean()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.existingBinding[Int] should not be defined
    }

    "be bound to Number with name" in {
      val (k, v) = rkey() -> Random.nextInt()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.instance[Number](Names.named(k)) shouldBe v
    }
  }

  "double bindings" should {
    "be bound by name" in {
      val (k, v) = rkey() -> Random.nextDouble()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.instance[Double](Names.named(k)) shouldBe v
    }

    "not be bound without name" in {
      val (k, v) = rkey() -> Random.nextBoolean()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.existingBinding[Double] should not be defined
    }

    "be bound to Number with name" in {
      val (k, v) = rkey() -> Random.nextInt()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.instance[Number](Names.named(k)) shouldBe v
    }
  }

  "long bindings" should {
    "be bound by name" in {
      val (k, v) = rkey() -> Random.nextLong()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.instance[Long](Names.named(k)) shouldBe v
    }

    "not be bound without name" in {
      val (k, v) = rkey() -> Random.nextBoolean()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.existingBinding[Long] should not be defined
    }

    "be bound to Number with name" in {
      val (k, v) = rkey() -> Random.nextInt()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.instance[Number](Names.named(k)) shouldBe v
    }
  }

  "boolean bindings" should {
    "be bound by name" in {
      val (k, v) = rkey() -> Random.nextBoolean()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.instance[Boolean](Names.named(k)) shouldBe v
    }

    "not be bound without name" in {
      val (k, v) = rkey() -> Random.nextBoolean()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.existingBinding[Boolean] should not be defined
    }

    "be bound to Number with name" in {
      val (k, v) = rkey() -> Random.nextInt()
      val inj = Guice.createInjector(NamedConfigSpec(k, v))
      inj.instance[Number](Names.named(k)) shouldBe v
    }
  }
}

object NamedConfigSpec {
  def apply[T](k: String, v: T): NamedConfigModule = {
    val cfg = ConfigFactory.parseString(s"$k=${v.toString}")
    NamedConfigModule(cfg)
  }

  def config[T](k: String, v: T): NamedConfigModule = {
    import scala.collection.JavaConverters._
    //val cfg = ConfigFactory.empty().withValue(k, ConfigValueFactory.fromMap(Map(k -> v.toString)))
    val cfg = ConfigFactory.parseMap(Map(k -> ConfigValueFactory.fromMap(Map(k -> v.toString, "x" -> "y").asJava)).asJava)
    NamedConfigModule(cfg)
  }

  // random key string
  def rkey() = Random.alphanumeric.take(10).mkString
}
