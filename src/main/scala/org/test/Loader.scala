package org.test

import pureconfig.ConfigReader.Result

import scala.reflect.ClassTag

object Loader {

  import pureconfig._

  def fromString[T: ClassTag](content: String)(implicit e: Exported[ConfigReader[T]]): Result[T] = {
    ConfigSource.string(content).load[T] // can load from env, system properties, file, resources, ...
  }

}
