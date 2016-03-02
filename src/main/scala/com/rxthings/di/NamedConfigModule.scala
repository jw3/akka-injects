package com.rxthings.di


import com.typesafe.config.{ConfigValue, Config}
import com.typesafe.config.ConfigValueType._
import com.typesafe.scalalogging.LazyLogging
import net.codingwell.scalaguice.ScalaModule

import scala.collection.JavaConversions._

/**
 * Bind Config values as @Named(path)
 */
object NamedConfigModule {
  def apply(config: Config) = new NamedConfigModule(config)
}

class NamedConfigModule private(config: Config) extends ScalaModule with LazyLogging {
  def configure(): Unit = {
    config.entrySet.foreach { e =>
      val k = e.getKey
      val v = e.getValue
      v.valueType() match {
        case NUMBER =>
          logger.trace("binding NUMBER value; [{}] -> [{}]", k, v)
          val num = v.unwrapped().asInstanceOf[Number]
          bind[Number].annotatedWithName(k).toInstance(num)
          bind[Double].annotatedWithName(k).toInstance(num.doubleValue())
          bind[Long].annotatedWithName(k).toInstance(num.longValue())
          bind[Int].annotatedWithName(k).toInstance(num.intValue())
        case BOOLEAN =>
          logger.trace("binding BOOLEAN value; [{}] -> [{}]", k, v)
          bind[Boolean].annotatedWithName(k).toInstance(unwrap(v))
        case STRING =>
          logger.trace("binding STRING value; [{}] -> [{}]", k, v)
          bind[String].annotatedWithName(k).toInstance(unwrap(v))
        case OBJECT =>
          logger.warn("Config OBJECT values are not supported, [{}]", k)
        case LIST =>
          logger.warn("Config List values are not supported, [{}]", k)
        case NULL =>
          logger.warn("Config Null values are not supported, [{}]", k)
      }
    }
  }

  def unwrap[T](v: ConfigValue): T = v.unwrapped().asInstanceOf[T]
}
