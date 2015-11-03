package wiii

import akka.actor.{Actor, ActorContext, ActorRef, ActorSystem}
import com.google.inject.Injector
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule


/**
 * goals:
 * val foo: ActorRef = InjectActor[IMyActor]
 * val bar: String = Inject[String] annotated "the.prop.path"
 * val baz: IBaz = Inject[IBaz]
 * val ctord: Bing = Inject[Bing] withArgs("name", 1001)
 * val actor: ActorRef = InjectActor[IMyActor] named "bob" specifiedAt "path.from.cfg"
 * val somthing: Option[Something] = Inject[Something] asOptional
 *
 * val composed: Bing = Inject[Bing] annotated "the.prop.path" withArgs("name", 1001) asOptional
 *
 * Note:
 * - 'named' and 'keyed' are mutually exclusive
 *
 * Using Manifest for now as ScalaGuice is still bound to them
 */
package object inject {
    import wiii.inject.Internals._

    type InjectorProvider = () => Injector

    def Inject[T: Manifest](implicit ip: InjectorProvider) = new InjectionBuilderImpl[T](ip)
    def InjectActor[T <: Actor: Manifest](implicit sys: ActorSystem, ctx: ActorContext = null) = new ActorInjectionBuilderImpl[T](sys, Option(ctx))

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

