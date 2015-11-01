package wiii.inject

import akka.actor.{Actor, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{Matchers, WordSpecLike}


class ActorScopeSpec extends TestKit(ActorSystem(classOf[ActorScopeSpec].getSimpleName.dropRight(1)))
                             with ImplicitSender with WordSpecLike with Matchers {

}

object ActorScopeSpec {
    //compiling is good enough test for now, shows the implicits are working
    class AnActor extends Actor {
        val name: String = Inject[String]

        def receive: Receive = {
            case _ =>
        }
    }
}
