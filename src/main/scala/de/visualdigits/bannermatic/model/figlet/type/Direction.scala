package de.visualdigits.bannermatic.model.figlet.`type`

import enumeratum._

sealed trait Direction extends EnumEntry

object Direction extends Enum[Direction] {

  val values: IndexedSeq[Direction] = findValues

  def valueOf(name: String): Direction = {
    name.toLowerCase() match {
      case "auto" => auto
      case "left_to_right" => left_to_right
      case "right_to_left" => right_to_left
      case _ => null
    }
  }

  case object auto extends Direction

  case object left_to_right extends Direction

  case object right_to_left extends Direction
}
