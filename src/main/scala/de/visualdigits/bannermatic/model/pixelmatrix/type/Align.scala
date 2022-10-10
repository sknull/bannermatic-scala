package de.visualdigits.bannermatic.model.pixelmatrix.`type`

import enumeratum._

sealed class Align(val v: Int = 0) extends EnumEntry {
}

object Align extends Enum[Align] {

  def apply(v: Int = 0) = new Align(v)

  val values: IndexedSeq[Align] = findValues

  def valueOf(name: String): Align = {
    name.toLowerCase() match {
      case "left" => left
      case "center" => center
      case "right" => right
      case _ => null
    }
  }

  case object left extends Align

  case object center extends Align

  case object right extends Align
}
