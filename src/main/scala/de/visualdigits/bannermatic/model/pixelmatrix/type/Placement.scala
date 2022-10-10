package de.visualdigits.bannermatic.model.pixelmatrix.`type`

import enumeratum._

sealed class Placement(val v: Int = 0) extends EnumEntry {
}

object Placement extends Enum[Placement] {

  def apply(v: Int = 0) = new Placement(v)

  val values: IndexedSeq[Placement] = findValues

  def valueOf(name: String): Placement = {
    name.toLowerCase() match {
      case "inside" => inside
      case "outside" => outside
      case _ => null
    }
  }

  case object inside extends Placement

  case object outside extends Placement
}
