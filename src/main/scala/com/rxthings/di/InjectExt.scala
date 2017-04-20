package com.rxthings.di

import java.util.ServiceLoader

import akka.actor._
import com.google.inject.util.Modules
import com.google.inject.{Guice, Injector, Module}
import com.typesafe.config.Config
import net.ceedubs.ficus.Ficus._
import net.codingwell.scalaguice.ScalaModule

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
 * Akka Extension which encapsulates a Guice Injector
 *
 * @param injector top level Guice Injector
 */
class InjectExtImpl(val injector: Injector) extends Extension

object InjectExt extends ExtensionId[InjectExtImpl] with ExtensionIdProvider with InjectExtBuilder {
  private val manualModules: ThreadLocal[mutable.Set[Module]] = ThreadLocal.withInitial(
    new java.util.function.Supplier[mutable.Set[Module]] {
      override def get(): mutable.Set[Module] = mutable.Set[Module]()
  })

  /**
   * Manually add modules to the injector
   * All modules must be added prior to creating the ActorSystem
   */
  def addModules(m: Module*): InjectExtBuilder = {
    manualModules.set(manualModules.get() ++ m)
    this
  }

  // internals \\

  override def createExtension(sys: ExtendedActorSystem) = {
    import InjectExtBuilder._

    val config = sys.settings.config
    val modules = config.getAs[String](ModuleDiscoveryModeKey).getOrElse(DefaultModuleDiscoveryModeMode) match {
      case ManualModuleDiscovery => manualModules.get().toList
      case CfgModuleDiscovery => config.getAs[Seq[String]](CfgModuleDiscoveryKey).map(_.map(strToModule)).getOrElse(Seq()).toList
      case SpiModuleDiscovery => ServiceLoader.load(classOf[Module]).iterator.asScala.toList
      case v => throw new IllegalArgumentException(s"invalid $ModuleDiscoveryModeKey value, $v")
    }
    manualModules.remove()

    val finalModules = addCfgModule(config) :: modules
    val defaultModules = Seq(Defaults.actorSystem(sys))
    val injector = Guice.createInjector(finalModules ++ defaultModules: _*)
    new InjectExtImpl(injector)
  }

  override def lookup() = InjectExt

  private def strToModule(fqcn: String): Module = {
    // todo;; should handle this in scala reflection to support objects
    Class.forName(fqcn).newInstance() match {
      case o: Module => o
      case o => throw new IllegalArgumentException(s"$o is not a com.google.inject.Module, $fqcn")
    }
  }
}

/**
 * Defines the module adding interface on the InjectExt
 */
trait InjectExtBuilder {
  this: ExtensionId[_] =>
  def addModules(m: Module*): InjectExtBuilder
}

/**
 * Configuration options for the InjectExt
 *
 * - ModuleDiscoveryModeKey: specifies the module discovery strategy [manual | config | spi]
 * - CfgModuleDiscoveryKey: specifies the FQCN list of Modules when in 'config' mode
 *
 * - ManualModuleDiscovery: discovery mode that only uses Modules added through InjectExtBuilder
 * - CfgModuleDiscovery: discovery mode that uses modules specified in the CfgModuleDiscoveryKey
 * - SpiModuleDiscovery: discovery mode that uses modules specified through SPI
 *
 * - injectConfigurationKey: specify whether to provide the application Config through the injector
 * - defaultInjectConfiguration: the default behavior of the injectConfigurationKey; which is true
 *
 * Notes:
 * - The default discovery mode is ManualModuleDiscovery
 */
object InjectExtBuilder {
  val ModuleDiscoveryModeKey = "akka.inject.mode"
  val CfgModuleDiscoveryKey = "akka.inject.modules"

  val ManualModuleDiscovery = "manual"
  val CfgModuleDiscovery = "config"
  val SpiModuleDiscovery = "spi"
  val DefaultModuleDiscoveryModeMode = ManualModuleDiscovery

  val injectConfigurationKey = "akka.inject.cfg"

  def addCfgModule(cfg: Config): Module = {
    cfg.getAs[Boolean](injectConfigurationKey).getOrElse(true) match {
      case true => new ConfigModule(cfg)
      case false => Modules.EMPTY_MODULE
    }
  }
}

private object Defaults {
  def actorSystem(sys: ActorSystem) = new ScalaModule {
    def configure(): Unit = bind[ActorSystem].toInstance(sys)
  }
}
