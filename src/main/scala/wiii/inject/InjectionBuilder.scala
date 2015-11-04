package wiii.inject

import akka.actor._
import com.google.inject.Injector
import net.codingwell.scalaguice.KeyExtensions._
import net.codingwell.scalaguice._
import wiii.inject.Internals._

import scala.util.Random


/**
 * Builder for non-Actor injections
 * @tparam T Type requested
 */
trait InjectionBuilder[T] extends BaseBuilder[T, T] {
    this: BaseInjectionBuilder[T, T] =>


    //\\ internals //\\
    protected type This = InjectionBuilder[T]
    protected def This() = this
}

/**
 * Builder for Actor injections
 * @tparam T Type requested
 */
trait ActorInjectionBuilder[T <: Actor] extends BaseBuilder[T, ActorRef] {
    this: BaseInjectionBuilder[T, ActorRef] =>

    def named(name: String): This

    //\\ internals //\\
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

    def arguments(args: Any*): This
    def annotated(name: String): This
    def specified(key: String): This

    def build: O
    def optional: Option[O]
}

//\\ internals //\\
private[inject] object Internals {
    abstract class BaseInjectionBuilder[I, O] extends BaseBuilder[I, O] {
        var ctorArgs: Option[CtorArgs] = None
        def arguments(args: Any*): This = {
            ctorArgs = Option(CtorArgs(args))
            This()
        }

        var annotatedName: Option[AnnotatedName] = None
        def annotated(name: String): This = {
            annotatedName = Option(AnnotatedName(name))
            This()
        }

        var specifiedWith: Option[SpecifiedWith] = None
        def specified(key: String): This = {
            specifiedWith = Option(SpecifiedWith(key))
            This()
        }

        protected def This(): This
    }

    class InjectionBuilderImpl[T: Manifest](ip: InjectorProvider)
        extends BaseInjectionBuilder[T, T] with InjectionBuilder[T] {

        implicit lazy val injector: Injector = ip()

        def build: T = optional.get
        def optional: Option[T] = provider[T].map(_.get())
    }

    class ActorInjectionBuilderImpl[T <: Actor : Manifest](sys: ActorSystem, ctx: Option[ActorContext])
        extends BaseInjectionBuilder[T, ActorRef] with ActorInjectionBuilder[T] {

        implicit lazy val injector: Injector = InjectExt(sys).injector

        var actorName: Option[ActorName] = None
        def named(name: String): ActorInjectionBuilder[T] = {
            actorName = Option(ActorName(name))
            This()
        }

        def build: ActorRef = optional.get
        def optional: Option[ActorRef] = {
            provider[T].map(p =>
                ctx.getOrElse(sys).actorOf(Props(p.get), actorName.map(_.name).getOrElse(randname))
            )
        }
    }

    def randname: String = Random.alphanumeric.take(10).mkString

    def provider[T: Manifest](implicit inj: Injector) =
        Option(inj.getExistingBinding(typeLiteral[T].toKey)).map(_.getProvider)


    case class CtorArgs(args: Any*)
    case class AnnotatedName(name: String)
    case class SpecifiedWith(key: String)
    case class ActorName(name: String)
}
