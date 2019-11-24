package de.visualdigits.bannermatic.model.pixelmatrix.`type`

import enumeratum._

sealed trait Location extends EnumEntry

object Location extends Enum[Location] {

  val values: IndexedSeq[Location] = findValues

  case object top extends Location
  case object bottom extends Location
  case object center extends Location
  case object left extends Location
  case object right extends Location
}
