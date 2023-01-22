package org.mauritania.main4ino.helpers

import org.scalatest._
import pureconfig._
import pureconfig.generic.auto._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.test.Classes.{CatType, DogType, PersonType}
import org.test.{Classes, Loader}
import pureconfig.error.CannotConvert
import pureconfig.generic.ProductHint

class LoaderSpec extends AnyFlatSpec with Matchers with EitherValues {

  "The config loader" should "parse a basic configuration" in {
    val fromString = Loader.fromString[Classes.IntAndString]("""
        |an-integer: 10 // kebab-case
        |a-string: toto
        |""".stripMargin)
    fromString shouldBe Right(Classes.IntAndString(10, "toto"))
  }

  it should "parse a basic config pascal-case to camel-case" in {
    implicit def productHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, PascalCase))
    val fromString = Loader.fromString[Classes.IntAndString]("""
        |AnInteger: 10
        |AString: toto
        |""".stripMargin)
    fromString shouldBe Right(Classes.IntAndString(10, "toto"))
  }

  it should "parse a config with a sealed trait (using 'type' keyword)" in {
    val fromString = Loader.fromString[Classes.Colleague]("""
                                                            |name: mauricio
                                                            |person-type: {type: dog-type}
                                                            |""".stripMargin)
    fromString shouldBe Right(Classes.Colleague("mauricio", DogType))
  }

  it should "parse a config with a sealed trait (custom reader string -> sealed trait)" in {
    implicit val personTypeReader: ConfigReader[PersonType] =
      ConfigReader[String].map(s => if (s.contains("dog")) DogType else CatType)

    val fromString = Loader.fromString[Classes.Colleague]("""
                                                    |name: mauricio
                                                    |person-type: dog
                                                    |""".stripMargin)
    fromString shouldBe Right(Classes.Colleague("mauricio", DogType))
  }

  it should "parse a config with a sealed trait (custom reader int -> sealed trait)" in {
    implicit val personTypeReader: ConfigReader[PersonType] =
      ConfigReader[Int].map(i => if (i == 1) DogType else CatType)

    val fromString = Loader.fromString[Classes.Colleague]("""
                                                            |name: mauricio
                                                            |person-type: 1
                                                            |""".stripMargin)
    fromString shouldBe Right(Classes.Colleague("mauricio", DogType))
  }

  it should "parse a config with a sealed trait (custom reader with enriched error)" in {
    implicit val personTypeReader: ConfigReader[PersonType] = ConfigReader[String].emap { s =>
      PersonType
        .fromString(s)
        .fold(
          errMsg => Left(CannotConvert(s, PersonType.getClass.getTypeName, errMsg)),
          s => Right(s) // you could add tighter validation here!
        )
    }
    val fromString = Loader.fromString[Classes.Colleague]("""
                                             |name: mauricio
                                             |person-type: zebra
                                             |""".stripMargin)
    fromString.left.value.head.description should include("Cannot convert 'zebra' to ")
  }

}
