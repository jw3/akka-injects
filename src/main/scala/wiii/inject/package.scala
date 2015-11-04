package wiii

import akka.actor.{Actor, ActorContext, ActorRef, ActorSystem}
import com.google.inject.Injector
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule


/**
 * the inject API
 */
package object inject {
    import wiii.inject.Internals._

    type InjectorProvider = () => Injector

    /**
     * entry point to the Inject of non-actors
     * @param ip Function providing an [[Injector]]
     * @return InjectionBuilder
     */
    def Inject[T: Manifest](implicit ip: InjectorProvider): InjectionBuilder[T] = {
        require(ip != null, "injection provider required")
        new InjectionBuilderImpl[T](ip)
    }

    /**
     * entry point to the Inject of actors
     * @param sys the ActorSystem (required)
     * @param ctx an ActorContext (optional)
     * @return ActorInjectionBuilder
     */
    def InjectActor[T <: Actor : Manifest](implicit sys: ActorSystem, ctx: ActorContext = null): ActorInjectionBuilder[T] = {
        require(sys != null, "actor system required")
        new ActorInjectionBuilderImpl[T](sys, Option(ctx))
    }

    /**
     * [[com.google.inject.Module]] that provides the application [[Config]]
     */
    class ConfigModule(cfg: Config) extends ScalaModule {
        def configure(): Unit = bind[Config].toInstance(cfg)
    }

    //\\ implicits //\\
    implicit def standardBuilder2built[T](builder: InjectionBuilder[T]): T = builder.build
    implicit def actorBuilder2actorRef[T <: Actor](builder: ActorInjectionBuilder[T]): ActorRef = builder.build
    implicit def actorSystem2injectorProvider(implicit sys: ActorSystem): InjectorProvider = () => InjectExt(sys).injector
    implicit def actorContext2actorSystem(implicit ctx: ActorContext): ActorSystem = ctx.system
}

