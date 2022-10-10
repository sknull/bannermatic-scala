package de.visualdigits.bannermatic.model.pixelmatrix

case class Pixel(
                  var fgColor: Color = Pixel.COLOR_DEFAULT,
                  var bgColor: Color = Pixel.COLOR_DEFAULT,
                  var char: String = Pixel.CHAR_DEFAULT,
                  asciiArt: Boolean = false,
                  grayscale: Boolean = false
                ) {

  this.fgColor.isBackground = false
  this.bgColor.isBackground = true

  override def clone(): Pixel = {
    Pixel(fgColor.clone(), bgColor.clone(), char, asciiArt = asciiArt, grayscale = grayscale)
  }

  override def toString: String = {
    if (grayscale && asciiArt) {
      char
    } else {
      fgColor.toString() + bgColor.toString() + char
    }
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
  val COLOR_DEFAULT: Color = Color("default")
  val CHAR_DEFAULT = " "
}
