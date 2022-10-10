package de.visualdigits.bannermatic.model.pixelmatrix.`type`

import enumeratum._

sealed class Inset(val v: Int = 0) extends EnumEntry {
}

object Inset extends Enum[Inset] {

  def apply(v: Int = 0) = new Inset(v)

  val values: IndexedSeq[Inset] = findValues

  case object top extends Inset

  case object right extends Inset

  case object bottom extends Inset

  case object left extends Inset
}
