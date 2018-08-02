package com.github.jw3.di

import com.github.jw3.di.MemberInjectionSpec._
import com.github.jw3.di.test.{BindingAnnotation1, BindingAnnotation2, InjectSpec}
import javax.inject.{Inject, Named}
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers

import scala.util.Random


/**
 * validates injecting members that are annotated
 */
class MemberInjectionSpec extends InjectSpec with Matchers {

  "annotated injection" should {
    injectTest("inject properties", M1) { implicit sys =>
      inject[PropertyInjected].required.intVal shouldBe MemberInjectionSpec.intVal
    }
    injectTest("inject named properties", M1) { implicit sys =>
      inject[NamedPropertyInjected].required.intVal shouldBe MemberInjectionSpec.namedIntVal
    }
    injectTest("inject properties into ctor", M1) { implicit sys =>
      inject[ConstructorInjected].required.intVal shouldBe MemberInjectionSpec.intVal
    }
    injectTest("inject named properties into ctor", M1) { implicit sys =>
      inject[ConstructorNamedInjected].required.intVal shouldBe MemberInjectionSpec.namedIntVal
    }
    injectTest("inject bound with anno properties", M1) { implicit sys =>
      val o = inject[OtherBindingInjected].required
      //o.intVal1 shouldBe MemberInjectionSpec.otherIntVal1
      o.intVal2 shouldBe MemberInjectionSpec.otherIntVal2
    }
  }
}

object MemberInjectionSpec {
  val name = "____NAME____"
  val intVal = Random.nextInt
  val namedIntVal = Random.nextInt
  val otherIntVal1 = Random.nextInt
  val otherIntVal2 = Random.nextInt

  class PropertyInjected {
    @Inject var intVal: Int = _
  }

  class NamedPropertyInjected {
    @Inject
    @Named("____NAME____")
    var intVal: Int = _
  }

  class OtherBindingInjected {
    @Inject
    @BindingAnnotation1
    var intVal1: Int = _

    @Inject
    @BindingAnnotation2
    var intVal2: Int = _
  }

  class ConstructorInjected @Inject()(val intVal: Int)
  class ConstructorNamedInjected @Inject()(@Named("____NAME____") val intVal: Int)

  object M1 extends ScalaModule {
    override def configure(): Unit = {
      bind[PropertyInjected]
      bind[NamedPropertyInjected]
      bind[ConstructorInjected]
      bind[ConstructorNamedInjected]
      bind[OtherBindingInjected]

      bind[Int].toInstance(intVal)
      bind[Int].annotatedWithName(name).toInstance(namedIntVal)
      bind[Int].annotatedWith[BindingAnnotation1].toInstance(otherIntVal1)
      bind[Int].annotatedWith[BindingAnnotation2].toInstance(otherIntVal2)
    }
  }
}
