package wiii.inject

import com.typesafe.config.Config
import net.codingwell.scalaguice.KeyExtensions._
import net.codingwell.scalaguice._
import org.scalatest.Matchers

/**
 * test various aspects of the [[InjectExt]] Akka Extension
 */
class ExtensionSpec extends InjectSpec with Matchers {
    "inject extension" should {
        injectTest("empty injector when no modules and no config", cfg = cfg(injCfg(false))) { implicit sys =>
            val inj = InjectExt(sys).injector
            inj.getExistingBinding(typeLiteral[Config].toKey) shouldBe null
        }

        injectTest("empty injector when no modules, with config (explicitly)", cfg = cfg(injCfg(true))) { implicit sys =>
            val inj = InjectExt(sys).injector
            inj.getExistingBinding(typeLiteral[Config].toKey) should not be null
        }

        injectTest("empty injector when no modules, with config (implicitly)") { implicit sys =>
            val inj = InjectExt(sys).injector
            inj.getExistingBinding(typeLiteral[Config].toKey) should not be null
        }
    }
}
