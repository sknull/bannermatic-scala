package de.visualdigits.bannermatic.model.figlet.`type`

import enumeratum._

sealed trait Justify extends EnumEntry

object Justify extends Enum[Justify] {

  val values: IndexedSeq[Justify] = findValues

  def valueOf(name: String): Justify = {
    name.toLowerCase() match {
      case "auto" => auto
      case "left" => left
      case "center" => center
      case "right" => right
      case _ => null
    }
  }

  case object auto extends Justify

  case object left extends Justify

  case object center extends Justify

  case object right extends Justify
}
