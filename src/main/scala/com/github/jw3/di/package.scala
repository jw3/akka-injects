package com.github.jw3

import akka.actor.{Actor, ActorContext, ActorRef, ActorSystem}
import com.google.inject.Injector

import scala.reflect.runtime.universe._


/**
 * the inject API
 */
package object di {
  import Internals._

  type InjectorProvider = () => Injector

  /**
   * entry point to the Inject of non-actors
   *
   * @param ip Function providing an Injector
   * @return InjectionBuilder
   */
  def inject[T: Manifest](implicit ip: InjectorProvider): InjectionBuilder[T] = {
    requireType[T](not[Actor])
    requireNonNull(ip, "injection provider required")
    new InjectionBuilderImpl[T](ip)
  }

  /**
   * entry point to the Inject of actors
   *
   * @param sys the ActorSystem (required)
   * @param ctx an ActorContext (optional)
   * @return ActorInjectionBuilder
   */
  def injectActor[T <: Actor : Manifest](implicit sys: ActorSystem, ctx: ActorContext = null): ActorInjectionBuilder[T] = {
    requireNonNull(sys, "actor system required")
    new ActorInjectionBuilderImpl[T](sys, Option(ctx))
  }

  //\\ implicits //\\
  implicit def standardBuilder2built[T](builder: InjectionBuilder[T]): T = builder.required
  implicit def actorBuilder2actorRef[T <: Actor](builder: ActorInjectionBuilder[T]): ActorRef = builder.required
  implicit def standardBuilder2builtOpt[T](builder: InjectionBuilder[T]): Option[T] = builder.optional
  implicit def actorBuilder2actorRefOpt[T <: Actor](builder: ActorInjectionBuilder[T]): Option[ActorRef] = builder.optional
  implicit def actorSystem2injectorProvider(implicit sys: ActorSystem): InjectorProvider = () => InjectExt(sys).injector

  //\\ internals //\\
  private def requireNonNull(o: Any, msg: => Any): Unit = require(o != null, msg)
  private def requireType[T: Manifest](fn: Type => Boolean) = {
    if (!fn(typeOf[T])) throw new UnsupportedOperationException(s"${typeOf[T]} cannot be created via this injection call")
  }
  private def is[Rhs: Manifest](lhs: Type) = lhs <:< typeOf[Rhs]
  private def not[Rhs: Manifest](lhs: Type) = !is[Rhs](lhs)
}

