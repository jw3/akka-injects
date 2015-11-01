package wiii.inject

import akka.actor.{Actor, ActorContext, ActorRef, ActorSystem}
import com.google.inject.Injector
import wiii.inject.Internals._


/**
 * Builder for non-Actor injections
 * @tparam T Type requested
 */
trait InjectionBuilder[T] extends BaseBuilder[T, T] {
    this: BaseInjectionBuilder[T, T] =>
    protected type This = InjectionBuilder[T]
    protected def This() = this
}

/**
 * Builder for Actor injections
 * @tparam T Type requested
 */
trait ActorInjectionBuilder[T <: Actor] extends BaseBuilder[T, ActorRef] {
    this: BaseInjectionBuilder[T, ActorRef] =>
    protected type This = ActorInjectionBuilder[T]
    protected def This() = this
}

/**
 * Base builder providing common building capabilities
 * @tparam I Requested Type ('I'nput)
 * @tparam O Expected Type ('O'utput)
 */
trait BaseBuilder[I, O] {
    protected type This

    def ctor(args: Any*): This
    def build: O
}

//\\ internals //\\
private[inject] object Internals {
    abstract class BaseInjectionBuilder[I, O] extends BaseBuilder[I, O] {
        def ctor(args: Any*): This = {
            ctorArgs = Option(CtorArgs(args))
            This()
        }

        private var ctorArgs: Option[CtorArgs] = None
        protected def This(): This
    }

    class InjectionBuilderImpl[T](ip: InjectorProvider)
        extends BaseInjectionBuilder[T, T] with InjectionBuilder[T] {

        lazy val injector: Injector = ip()
        def build: T = ???
    }

    class ActorInjectionBuilderImpl[T <: Actor](sys: ActorSystem, ctx: Option[ActorContext])
        extends BaseInjectionBuilder[T, ActorRef] with ActorInjectionBuilder[T] {

        lazy val injector: Injector = InjectExt(sys).injector
        def build: ActorRef = ???
    }

    case class CtorArgs(args: Any*)
}
