package com.rxthings.di.test

import java.util.UUID

import net.codingwell.scalaguice.ScalaModule

import scala.util.Random


object SpiTests {
  val stringVal = UUID.randomUUID().toString
  val intVal = Random.nextInt
}

class SpiModule1 extends ScalaModule {
  def configure(): Unit = bind[String].toInstance(SpiTests.stringVal)
}

class SpiModule2 extends ScalaModule {
  def configure(): Unit = bind[Int].toInstance(SpiTests.intVal)
}
