package wiii.inject

import java.util.ServiceLoader

import akka.actor.{ExtendedActorSystem, Extension, ExtensionId, ExtensionIdProvider}
import com.google.inject.{Guice, Injector, Module}
import net.ceedubs.ficus.Ficus._

import scala.collection.JavaConversions._


class InjectExtImpl(val injector: Injector) extends Extension

object InjectExt
    extends ExtensionId[InjectExtImpl] with ExtensionIdProvider with InjectExtBuilder {

    private val manualModules: collection.mutable.Set[Module] = collection.mutable.Set[Module]()

    def addModules(m: Module*): InjectExtBuilder = {
        manualModules.addAll(m)
        this
    }

    def clearModules: InjectExtBuilder = {
        manualModules.clear()
        this
    }

    override def createExtension(sys: ExtendedActorSystem) = {
        import InjectExtBuilder._

        val config = sys.settings.config
        val modules = config.getAs[String](ModuleDiscoveryModeKey).getOrElse(DefaultModuleDiscoveryModeMode) match {
            case ManualModuleDiscovery => manualModules.toList
            case CfgModuleDiscovery => config.getAs[Seq[String]](CfgModuleDiscoveryKey).map(_.map(strToModule)).getOrElse(Seq()).toList
            case SpiModuleDiscovery => ServiceLoader.load(classOf[Module]).toList
            case v => throw new IllegalArgumentException(s"invalid $ModuleDiscoveryModeKey value, $v")
        }

        val injector = Guice.createInjector(new ConfigModule(config) :: modules)
        new InjectExtImpl(injector)
    }


    override def lookup = InjectExt

    private def strToModule(fqcn: String): Module = {
        val o = Class.forName(fqcn).newInstance()
        if (o.isInstanceOf[Module]) o.asInstanceOf[Module]
        else throw new IllegalArgumentException(s"not a module, $fqcn")
    }
}

trait InjectExtBuilder {
    self: ExtensionId[_] =>

    def addModules(m: Module*): InjectExtBuilder
    def clearModules: InjectExtBuilder
}

object InjectExtBuilder {
    val ModuleDiscoveryModeKey = "akka.inject.mode"
    val CfgModuleDiscoveryKey = "akka.inject.modules"

    val ManualModuleDiscovery = "manual"
    val CfgModuleDiscovery = "config"
    val SpiModuleDiscovery = "spi"
    val DefaultModuleDiscoveryModeMode = ManualModuleDiscovery
}
