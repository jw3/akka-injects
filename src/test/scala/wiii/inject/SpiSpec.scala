package wiii.inject

import net.codingwell.scalaguice.InjectorExtensions._
import org.scalatest.Matchers
import wiii.inject.test.{InjectSpec, SpiTests}

class SpiSpec extends InjectSpec with Matchers {

    "SPI injection" should {
        injectTest("load modules defined in service files", cfg = cfg(spiMode)) { implicit sys =>
            injector.instance[String] shouldBe SpiTests.stringVal
            injector.instance[Int] shouldBe SpiTests.intVal
        }
    }
}
