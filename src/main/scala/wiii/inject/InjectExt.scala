package wiii.inject

import java.util.ServiceLoader

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.google.inject.{Guice, Injector, Module}
import net.ceedubs.ficus.Ficus._

import scala.collection.JavaConversions._

/**
 * Akka [[Extension]] which encapsulates a Guice [[Injector]]
 * @param injector
 */
class InjectExtImpl(val injector: Injector) extends Extension

object InjectExt extends ExtensionId[InjectExtImpl] with ExtensionIdProvider with InjectExtBuilder {
    private val manualModules: collection.mutable.Set[Module] = collection.mutable.Set[Module]()

    /**
     * Manually add modules to the injector
     * All modules must be added prior to creating the ActorSystem
     */
    def addModules(m: Module*): InjectExtBuilder = {
        manualModules.addAll(m)
        this
    }

    // internals \\

    override def createExtension(sys: ExtendedActorSystem) = {
        import InjectExtBuilder._

        val config = sys.settings.config
        val modules = config.getAs[String](ModuleDiscoveryModeKey).getOrElse(DefaultModuleDiscoveryModeMode) match {
            case ManualModuleDiscovery => manualModules.toList
            case CfgModuleDiscovery => config.getAs[Seq[String]](CfgModuleDiscoveryKey).map(_.map(strToModule)).getOrElse(Seq()).toList
            case SpiModuleDiscovery => ServiceLoader.load(classOf[Module]).toList
            case v => throw new IllegalArgumentException(s"invalid $ModuleDiscoveryModeKey value, $v")
        }

        val finalModules = new ConfigModule(config) :: modules
        val injector = Guice.createInjector(finalModules)
        new InjectExtImpl(injector)
    }

    override def lookup = InjectExt

    private def strToModule(fqcn: String): Module = {
        val o = Class.forName(fqcn).newInstance()
        if (o.isInstanceOf[Module]) o.asInstanceOf[Module]
        else throw new IllegalArgumentException(s"not a module, $fqcn")
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
 * - CfgModuleDiscovery: discovery mode that includes modules specified in the CfgModuleDiscoveryKey
 * - SpiModuleDiscovery: discovery mode that includes modules specified through SPI
 *
 * Notes:
 * - All modes will include manually added modules
 * - The default discovery mode is ManualModuleDiscovery
 */
object InjectExtBuilder {
    val ModuleDiscoveryModeKey = "akka.inject.mode"
    val CfgModuleDiscoveryKey = "akka.inject.modules"

    val ManualModuleDiscovery = "manual"
    val CfgModuleDiscovery = "config"
    val SpiModuleDiscovery = "spi"
    val DefaultModuleDiscoveryModeMode = ManualModuleDiscovery
}
