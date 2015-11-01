package wiii.inject

import akka.actor.{Actor, ActorContext, ActorRef, ActorSystem}
import com.google.inject.Injector

trait BaseInjectionBuilder[I, O] {
    type This
    def ctor(args: Any*): This
    def build: O
}

trait StdInjectionBuilder[T] extends BaseInjectionBuilder[T, T] {
    type This = StdInjectionBuilder[T]
}

trait ActorInjectionBuilder[T <: Actor] extends BaseInjectionBuilder[T, ActorRef] {
    type This = ActorInjectionBuilder[T]
}

private[inject] class ActorInjectionBuilderImpl[T <: Actor](sys: ActorSystem, ctx: Option[ActorContext]) extends ActorInjectionBuilder[T] {
    lazy val injector: Injector = InjectExt(sys).injector
    var ctorArgs: Option[CtorArgs] = None

    def ctor(args: Any*): This = {
        ctorArgs = Option(CtorArgs(args))
        this
    }

    def build: ActorRef = ???
}

private case class CtorArgs(args: Any*)
