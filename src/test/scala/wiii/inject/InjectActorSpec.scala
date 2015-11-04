package wiii.inject

import akka.actor.{Actor, ActorRef}
import net.codingwell.scalaguice._
import org.scalatest.Matchers
import wiii.inject.test.{NopActor, InjectSpec}


class InjectActorSpec extends InjectSpec with Matchers {
    import InjectActorSpec._

    "actor injection" should {
        injectTest("should work with traits", Seq(SimpleModule)) { implicit sys =>
            val aa: ActorRef = InjectActor[IAnActor]
            aa should not be null
        }
    }

}

object InjectActorSpec {
    trait IAnActor extends NopActor
    class AnActor extends IAnActor

    object SimpleModule extends ScalaModule {
        def configure(): Unit = bind[IAnActor].to[AnActor]
    }
}
