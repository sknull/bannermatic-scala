package de.visualdigits.bannermatic.model.pixelmatrix

class Color(val name: String = "", var isBackground: Boolean = false, var red: Int, var green: Int, var blue: Int, var alpha: Int) {

  init()

  def init(): Unit = {
    if (name.nonEmpty) {
      val split: Array[String] = name.split(';')
      if (split.length > 2) {
        this.red = split(0).toInt
        this.green = split(1).toInt
        this.blue = split(2).toInt
      }
      if (split.length > 3) {
        this.alpha = split(3).toInt
      }
      if (name.startsWith("#")) {
        this.red = Integer.valueOf(name.substring(1, 3), 16)
        this.green = Integer.valueOf(name.substring(3, 5), 16)
        this.blue = Integer.valueOf(name.substring(5, 7), 16)
      }
    }
  }

  override def toString: String = {
    val uname = name.toUpperCase()
    if (name.nonEmpty && Color.COLOR_CODES.contains(uname)) {
      var ansiCode =  Color.COLOR_CODES(uname)
      if (isBackground) ansiCode += 10
      PixelMatrixConstants.ESCAPE + "[%dm".format(ansiCode)
    } else {
      var ansiCode = PixelMatrixConstants.CODE_FG
      if (isBackground) ansiCode += 10
      PixelMatrixConstants.ESCAPE + "[%d;2;%d;%d;%dm".format(ansiCode, red, green, blue)
    }
  }

  override def clone(): Color = {
    Color(name, isBackground, red, green, blue, alpha)
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[Color]

  override def equals(other: Any): Boolean = other match {
    case that: Color =>
      (that canEqual this) &&
        red == that.red &&
        green == that.green &&
        blue == that.blue &&
        alpha == that.alpha &&
        name == that.name
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(red, green, blue, alpha, name)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object Color {
  def apply(name: String = "", isBackground: Boolean = false, red: Int = 0, green: Int = 0, blue: Int = 0, alpha: Int = 0) = new Color(name, isBackground, red, green, blue, alpha)

  val RED: Color = Color("RED" )
  val GREEN: Color = Color("GREEN")
  val YELLOW: Color = Color("YELLOW")
  val BLUE: Color = Color("BLUE")
  val MAGENTA: Color = Color("MAGENTA")
  val CYAN: Color  = Color("CYAN")
  val LIGHT_GRAY: Color = Color("LIGHT_GRAY")
  val DEFAULT: Color = Color("DEFAULT")
  val DARK_GRAY: Color = Color("DARK_GRAY")
  val LIGHT_RED: Color = Color("LIGHT_RED")
  val LIGHT_GREEN: Color = Color("LIGHT_GREEN")
  val LIGHT_YELLOW: Color = Color("LIGHT_YELLOW")
  val LIGHT_BLUE: Color = Color("LIGHT_BLUE")
  val LIGHT_MAGENTA: Color = Color("LIGHT_MAGENTA")
  val LIGHT_CYAN: Color = Color("LIGHT_CYAN")
  val WHITE: Color = Color(red=255, green=255, blue=255)
  val BLACK: Color = Color()

  val COLOR_CODES: Map[String, Int] = Map[String, Int](
    "BLACK" -> 30,
    "RED" -> 31,
    "GREEN" -> 32,
    "YELLOW" -> 33,
    "BLUE" -> 34,
    "MAGENTA" -> 35,
    "CYAN" -> 36,
    "LIGHT_GRAY" -> 37,
    "DEFAULT" -> 39,
    "DARK_GRAY" -> 90,
    "LIGHT_RED" -> 91,
    "LIGHT_GREEN" -> 92,
    "LIGHT_YELLOW" -> 93,
    "LIGHT_BLUE" -> 94,
    "LIGHT_MAGENTA" -> 95,
    "LIGHT_CYAN" -> 96,
    "WHITE" -> 97,
    "RESET" -> 0
  )
}
