package wiii.inject

import akka.actor.{Actor, ActorRef}
import com.typesafe.config.Config
import net.codingwell.scalaguice.ScalaModule
import org.scalatest.Matchers
import wiii.inject.ExtensionSpec.TestModule

class ExtensionSpec extends InjectSpec with Matchers {
    injectTest("empty inject") { sys =>
        val inj = InjectExt(sys)
        inj.injector.getInstance(classOf[String]) shouldBe empty
    }

    injectTest("test module inject", Seq(TestModule)) { sys =>
        val inj = InjectExt(sys)
        inj.injector.getInstance(classOf[String]) shouldBe "foo"
    }

    injectTest("fooo", Seq(TestModule)) { implicit sys =>
        val a: ActorRef = InjectActor[Actor]
        val x: String = Inject[String]
    }

    injectTest("Config Test") { implicit sys =>
        val cfg: Config = Inject[Config]
        cfg should not be null
    }
}

object ExtensionSpec {
    object TestModule extends ScalaModule {
        def configure(): Unit = {
            bind[String].toInstance("foo")
        }
    }
}
