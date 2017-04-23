package com.rxthings.di

import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule


/**
 * Module that provides the application Config
 */
class ConfigModule(cfg: Config) extends ScalaModule {
  def configure(): Unit = {
    bind[Config].toInstance(cfg)
    install(NamedConfigModule(cfg))
  }
}
