package de.visualdigits.bannermatic.model.pixelmatrix.`type`

import enumeratum._

sealed class VAlign(val v: Int = 0) extends EnumEntry {
}

object VAlign extends Enum[VAlign] {

  def apply(v: Int = 0) = new VAlign(v)

  val values: IndexedSeq[VAlign] = findValues

  def valueOf(name: String): VAlign = {
    name.toLowerCase() match {
      case "top" => top
      case "middle" => middle
      case "bottom" => bottom
      case _ => null
    }
  }

  case object top extends VAlign

  case object middle extends VAlign

  case object bottom extends VAlign
}
