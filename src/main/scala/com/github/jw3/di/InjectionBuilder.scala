package com.github.jw3.di

import javax.inject.Provider

import akka.actor._
import com.google.inject.{Injector, Key, TypeLiteral}
import Internals.BaseInjectionBuilder
import net.codingwell.scalaguice.InjectorExtensions._
import net.codingwell.scalaguice.KeyExtensions._
import net.codingwell.scalaguice._

import scala.util.{Failure, Random, Success, Try}


/**
 * Builder for non-Actor injections
 *
 * @tparam T Type requested
 */
trait InjectionBuilder[T] extends BaseBuilder[T, T] {
  this: BaseInjectionBuilder[T, T] =>

  //\\ internals //\\
  protected type Builder = InjectionBuilder[T]
}

/**
 * Builder for Actor injections
 *
 * @tparam T Type requested
 */
trait ActorInjectionBuilder[T <: Actor] extends BaseBuilder[T, ActorRef] {
  this: BaseInjectionBuilder[T, ActorRef] =>

  def named(name: String): Builder

  //\\ internals //\\
  protected type Builder = ActorInjectionBuilder[T]
}

/**
 * Base builder providing common building capabilities
 *
 * @tparam I Requested Type ('I'nput)
 * @tparam O Expected Type ('O'utput)
 */
trait BaseBuilder[I, O] {
  protected type Builder

  def args(args: Any*): Builder
  def annotated(name: String): Builder

  def required: O
  def optional: Option[O]

  //\\ internals //\\
  protected def ThisBuilder(): Builder
}

//\\ internals //\\
private[di] object Internals {
  abstract class BaseInjectionBuilder[I, O] extends BaseBuilder[I, O] {
    var ctorArgs: Option[CtorArgs] = None
    def args(args: Any*): Builder = {
      ctorArgs = Option(CtorArgs(args))
      ThisBuilder()
    }

    var annotatedName: Option[AnnotatedName] = None
    def annotated(name: String): Builder = {
      annotatedName = Option(AnnotatedName(name))
      ThisBuilder()
    }

    protected def ThisBuilder(): Builder = this.asInstanceOf[Builder]
  }

  class InjectionBuilderImpl[T: Manifest](ip: InjectorProvider)
    extends BaseInjectionBuilder[T, T] with InjectionBuilder[T] {

    implicit lazy val injector: Injector = ip()

    def required: T = checkopt(optional) match {
      case Success(t) => t
      case Failure(ex@InjectionNotAvailable(_)) =>
        throw ex.copy(name = annotatedName)
      case Failure(ex) =>
        throw ex
    }

    def optional: Option[T] = provider[T](annotatedName.map(_.name)).map(_.get())
  }

  class ActorInjectionBuilderImpl[T <: Actor : Manifest](sys: ActorSystem, ctx: Option[ActorContext])
    extends BaseInjectionBuilder[T, ActorRef] with ActorInjectionBuilder[T] {

    implicit lazy val injector: Injector = InjectExt(sys).injector

    var actorName: Option[ActorName] = None
    def named(name: String): ActorInjectionBuilder[T] = {
      actorName = Option(ActorName(name))
      ThisBuilder()
    }

    def required: ActorRef = checkopt(optional) match {
      case Success(ref) => ref
      case Failure(ex@InjectionNotAvailable(_)) =>
        throw ex.copy(name = annotatedName)
      case Failure(ex) =>
        throw ex
    }


    def optional: Option[ActorRef] = {
      provider[T](annotatedName.map(_.name)).map(p =>
        ctx.getOrElse(sys).actorOf(Props(p.get), actorName.map(_.name).getOrElse(randname))
      )
    }
  }

  @throws[IllegalStateException]("If injection of [T] is not available")
  def checkopt[T: Manifest](opt: Option[T]): Try[T] = opt match {
    case Some(t) => Success(t)
    case None => Failure(InjectionNotAvailable[T]())
  }

  def randname: String = Random.alphanumeric.take(10).mkString

  def provider[T: Manifest](annotated: Option[String] = None)(implicit inj: Injector): Option[Provider[T]] = {
    annotated match {
      case None => prov(stdKey[T])
      case Some(anno) =>
        prov(annoKey[T](anno)) match {
          case opt@Some(_) => opt
          case None => throw new IllegalStateException(s"no provider for annotation @Named($anno)")
        }
    }
  }

  def prov[T: Manifest](keyFn: TypeLiteral[T] => Key[T])(implicit inj: Injector): Option[Provider[T]] = {
    inj.existingBinding(keyFn(typeLiteral[T])).map(_.getProvider)
  }

  def stdKey[T: Manifest](tlit: TypeLiteral[T]) = tlit.toKey
  def annoKey[T: Manifest](anno: String)(tlit: TypeLiteral[T]) = tlit.annotatedWithName(anno)

  case class CtorArgs(args: Any*)
  case class AnnotatedName(name: String)
  case class ActorName(name: String)

  case class InjectionNotAvailable[T: Manifest](name: Option[AnnotatedName] = None) extends IllegalStateException {
    override def getMessage: String = {
      s"Injection of [${manifest[T].runtimeClass.getName}] is not available" + name.map(n => s" for @Named(${n.name})").getOrElse("")
    }
  }
}
