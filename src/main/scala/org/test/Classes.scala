package org.test

object Classes {

  case class IntAndString(
    anInteger: Int,
    aString: String
  )

  object PersonType {
    def fromString(s: String): Either[String, PersonType] = {
      s match {
        case "dog" => Right(DogType)
        case "cat" => Right(CatType)
        case x => Left(s"Invalid value '${x}' (must be 'dog' or 'cat')")
      }
    }
  }
  sealed trait PersonType
  case object DogType extends PersonType
  case object CatType extends PersonType
  case class Colleague(
    name: String,
    personType: PersonType
  )

}
