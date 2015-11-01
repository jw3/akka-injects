package wiii.inject

import akka.actor.ActorSystem
import com.google.inject.Module
import org.scalatest.WordSpecLike

/**
 * utility for wrapping up guice based actor system tests
 */
trait InjectSpec extends WordSpecLike {

    def injectTest(name: String)(test: ActorSystem => Unit): Unit = injectTest(name, Seq())(test)

    def injectTest(name: String, mods: => Seq[Module] = Seq())(test: ActorSystem => Unit): Unit = {
        registerTest(name) {
            InjectExt.addModules(mods: _*)
            val sys = ActorSystem(s"${name.replaceAll(" ", "_")}")
            try test(sys)
            finally {sys.terminate().value}
        }
    }
}
