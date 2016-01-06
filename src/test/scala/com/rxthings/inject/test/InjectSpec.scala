package com.rxthings.inject.test

import akka.actor.ActorSystem
import com.google.inject.{Injector, Module}
import com.rxthings.inject.InjectExt
import com.rxthings.inject.InjectExtBuilder._
import com.typesafe.config.{Config, ConfigFactory, ConfigValueFactory}
import org.scalatest.WordSpecLike

import scala.collection.JavaConversions._
import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
 * utility for wrapping up guice based actor system tests
 */
trait InjectSpec extends WordSpecLike {
    /**
     * Injection test scope controller that creates an Injector and provides a clean ActorSystem per call
     * Allows for some modifications to the ActorSystem
     * - pass Config object that will used in construction
     */
    def injectTest(name: String, mods: => Seq[Module] = Seq(), cfg: Option[Config] = None)(test: ActorSystem => Unit): Unit = {
        registerTest(name) {
            InjectExt.addModules(mods: _*)
            val sys = ActorSystem(s"${name.replaceAll( """\W""", "_")}", cfg)
            try test(sys)
            finally {Await.ready(sys.terminate(), Duration.create("10s"))}
        }
    }

    def injector(implicit sys: ActorSystem): Injector = InjectExt(sys).injector

    /**
     * Compact configuration manipulation
     */
    def cfg(parts: (String, _ <: AnyRef)*): Config = ConfigFactory.parseMap(parts.toMap[String, AnyRef])

    /**
     * Specify whether to make the Config object available as an injection
     */
    def injCfg(f: Boolean) = injectConfigurationKey -> Boolean.box(f)
    def modMode(key: String) = ModuleDiscoveryModeKey -> key
    def cfgMode = modMode(CfgModuleDiscovery)
    def spiMode = modMode(SpiModuleDiscovery)
    def modCfg(mfqcn: String*) = CfgModuleDiscoveryKey -> ConfigValueFactory.fromIterable(mfqcn)


    //\\ implicits //\\
    implicit def cfg2Opt(cfg: Config): Option[Config] = Option(cfg)
    implicit def mod2Seq(mod: Module): Seq[Module] = Seq(mod)
}
