package de.visualdigits.bannermatic.model.pixelmatrix

class Pixel(var fgColor: Color = Pixel.COLOR_DEFAULT, var bgColor: Color = Pixel.COLOR_DEFAULT, var char: Char = Pixel.CHAR_DEFAULT) {

  this.fgColor.isBackground = false
  this.bgColor.isBackground = true

  override def clone(): Pixel = {
     Pixel(fgColor.clone(), bgColor.clone(), char)
  }

  override def toString: String = {
    fgColor.toString() + bgColor.toString() + char
  }

  def hasSameColors(other: Pixel): Boolean = other match {
    case that: Pixel => fgColor == that.fgColor && bgColor == that.bgColor
    case _ => false
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Pixel]

  override def equals(other: Any): Boolean = other match {
    case that: Pixel =>
      (that canEqual this) &&
        fgColor == that.fgColor &&
        bgColor == that.bgColor &&
        char == that.char
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(fgColor, bgColor, char)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object Pixel {
  def apply(fgColor: Color = Pixel.COLOR_DEFAULT, bgColor: Color = Pixel.COLOR_DEFAULT, char: Char = Pixel.CHAR_DEFAULT) = new Pixel(fgColor, bgColor, char)

  val COLOR_DEFAULT = Color("default")
  val CHAR_DEFAULT = ' '
}
