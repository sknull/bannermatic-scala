package de.visualdigits.bannermatic.model.pixelmatrix

import de.visualdigits.bannermatic.model.pixelmatrix.`type`.{Align, Inset, Placement, VAlign}

import scala.collection.mutable

class PixelMatrix(var width: Int = 0,
                  var height: Int = 0,
                  var char: Char = Pixel.CHAR_DEFAULT,
                  var fgColor: Color = Pixel.COLOR_DEFAULT,
                  var bgColor: Color = Pixel.COLOR_DEFAULT,
                  value: Array[Array[Char]] = Array[Array[Char]](),
                  flags: Set[String] = Set(),
                  offX: Int = 0,
                  offY: Int = 0,
                  grow: Boolean = true,
                  other: Option[PixelMatrix] = Option.empty) {

  private var matrix: Array[Array[Pixel]] = Array[Array[Pixel]]()

  if (value.nonEmpty) {
    initMatrix(width, height, value, offX, offY, grow)
  } else if (other.nonEmpty) {
    val o = other.get
    initMatrix(width, height, o.getValue, offX, offY, grow)
    for (y <- 0 until o.height) {
      for (x <- 0 until o.width) {
        val op = o.matrix(x)(y)
        setFgColor(x + offX)(y + offY)(op.fgColor)
        setBgColor(x + offX)(y + offY)(op.bgColor)
      }
    }
  } else {
    initMatrix(width, height)
  }

  override def clone(): PixelMatrix = {
    val c = PixelMatrix(width, height, char, fgColor, bgColor, getValue, flags)
    for (y <- 0 until height) for (x <- 0 until width) c.matrix(x)(y) = matrix(x)(y).clone()
    c
  }

  private def initMatrix(width: Int = 0, height: Int = 0, value: Array[Array[Char]] = Array[Array[Char]](), offX: Int = 0, offY: Int = 0, grow: Boolean = true): Unit = {
    if (value.nonEmpty) {
      val w = value.map(_.length).max
      this.width = if (grow) Math.max(width, w) else Math.min(width, w)
      val h = value.length
      this.height = if (grow) Math.max(height, h) else Math.min(height, h)
      this.matrix = Array.ofDim[Pixel](width, height)
      for (y <- 0 until this.height) for (x <- 0 until this.width) this.matrix(x)(y) = Pixel(fgColor, bgColor, char)
      if (grow) {
        for (y <- 0 until h) {
          val row = value(y)
          val w = row.length
          for (x <- 0 until w) {
            this.matrix(offX + x)(offY + y) = Pixel(fgColor, bgColor, row(x))
          }
        }
      }
      else for (y <- 0 until this.height) for (x <- 0 until this.width) this.matrix(x)(y) = Pixel(fgColor, bgColor, value(y + offY)(x + offX))
    } else {
      this.width = width
      this.height = height
      this.matrix = Array.ofDim[Pixel](width, height)
      for (y <- 0 until height) for (x <- 0 until width) this.matrix(x)(y) = Pixel(fgColor, bgColor, char)
    }
  }

  override def toString: String = {
    val rows = mutable.ListBuffer[String]()
    val prefix = fgColor.toString + bgColor.toString + flags.mkString("")
    for (y <- 0 until height) {
      var row = prefix
      var prevPixel = Pixel()
      for (x <- 0 until width) {
        val pixel = matrix(x)(y)
        if (!pixel.hasSameColors(prevPixel)) {
          row += pixel.toString
          prevPixel = pixel
        }
        else row += pixel.char
      }
      rows += row
    }
    rows.mkString(PixelMatrixConstants.RESET + "\n") + PixelMatrixConstants.RESET + "\n"
  }

  def pad(location: Inset, amount: Int, char: Char = ' '): PixelMatrix = {
    location match {
      case Inset.top => PixelMatrix(width, height + amount, char, fgColor, bgColor, flags = flags, offY = amount, other = Some(this))
      case Inset.bottom => PixelMatrix(width, height + amount, char, fgColor, bgColor, flags = flags, other = Some(this))
      case Inset.left => PixelMatrix(width + amount, height, char, fgColor, bgColor, flags = flags, offX = amount, other = Some(this))
      case Inset.right => PixelMatrix(width + amount, height, char, fgColor, bgColor, flags = flags, other = Some(this))
      case _ => clone()
    }
  }

  def trim(location: Inset, amount: Int): PixelMatrix = {
    location match {
      case Inset.top => PixelMatrix(width, height - amount, char, fgColor, bgColor, getValue, flags, offY = amount, grow = false)
      case Inset.bottom => PixelMatrix(width, height - amount, char, fgColor, bgColor, getValue, flags, grow = false)
      case Inset.left => PixelMatrix(width - amount, height, char, fgColor, bgColor, getValue, flags, offX = amount, grow = false)
      case Inset.right => PixelMatrix(width - amount, height, char, fgColor, bgColor, getValue, flags, grow = false)
      case _ => clone()
    }
  }

  def overlay(horizontal: Align, vertical: VAlign, overlay: PixelMatrix, placement: Placement = Placement.inside): PixelMatrix = {
    var p = clone()

    p = placement match {
      case Placement.outside =>
        p = horizontal match {
          case Align.left => p.pad(Inset.left, overlay.width)
          case Align.right => p.pad(Inset.right, overlay.width)
          case _ => p
        }
        p = vertical match {
          case VAlign.top => p.pad(Inset.top, overlay.height)
          case VAlign.bottom => p.pad(Inset.bottom, overlay.height)
          case _ => p
        }
        p
      case _ => p
    }

    p.bgColor = Color.DEFAULT
    val pc = p.columns()
    val pr = p.rows()

    val o = overlay.clip()
    val oc = o.columns()
    val or = o.rows()

    var offX = pc - oc
    if (offX < 0) {
      p = horizontal match {
        case Align.left => p.pad(Inset.right, -1 * offX)
        case Align.center => p.pad(Inset.left, (offX / -2.0 + 0.5).toInt).pad(Inset.right, (offX / -2.0 + 0.5).toInt)
        case Align.right => p.pad(Inset.left, -1 * offX)
        case _ => p
      }
      offX = 0
    } else {
      offX = horizontal match {
        case Align.left => 0
        case Align.center => offX / 2
        case _ => if (horizontal.v > 0) horizontal.v else offX
      }
    }

    var offY = pr - or
    if (offY < 0) {
      p = vertical match {
        case VAlign.top => p.pad(Inset.bottom, -1 * offY)
        case VAlign.middle => p.pad(Inset.top, offY / -2).pad(Inset.bottom, offY / -2)
        case VAlign.bottom => p.pad(Inset.top, -1 * offY)
        case _ => p
      }
      offY = 0
    } else {
      offY = vertical match {
        case VAlign.top => 0
        case VAlign.middle => offY / 2
        case _ => if (vertical.v > 0) vertical.v else offY
      }
    }

    for (y <- 0 until or) {
      for (x <- 0 until oc) {
        val op = o.matrix(x)(y)
        p.setChar(x + offX)(y + offY)(op.char)
        p.setFgColor(x + offX)(y + offY)(op.fgColor)
      }
    }
    p
  }

  def rows(): Int = {
    if (matrix.nonEmpty) matrix(0).length else 0
  }

  def columns(): Int = {
    matrix.length
  }

  def rowEmpty(row: Int, char: Char = ' '): Boolean = {
    matrix.forall(_(row).char == char)
  }

  def rowNonEmpty(row: Int, char: Char = ' '): Boolean = {
    matrix.exists(_(row).char != char)
  }

  def columnEmpty(column: Int, char: Char = ' '): Boolean = {
    matrix(column).forall(_.char == char)
  }

  def columnNonEmpty(column: Int, char: Char = ' '): Boolean = {
    matrix(column).exists(_.char != char)
  }

  def inset(amount: Int, aspect: Double = 2.0, char: Char = ' '): PixelMatrix = {
    clone()
      .pad(Inset.top, amount, char)
      .pad(Inset.bottom, amount, char)
      .pad(Inset.left, (aspect * amount).toInt, char)
      .pad(Inset.right, (aspect * amount).toInt, char)
  }

  def clip(char: Char = ' '): PixelMatrix = {
    var p = clone()
    val nc = columns() - 1
    val nr = rows() - 1
    val (left, top, right, bottom) = boundingBox(char)
    if (top > 0) p = p.trim(Inset.top, top)
    if (bottom < nr) p = p.trim(Inset.bottom, nr - bottom)
    if (left > 0) p = p.trim(Inset.left, left)
    if (right < nc) p = p.trim(Inset.right, nc - right)
    p
  }

  def boundingBox(char: Char = ' '): (Int, Int, Int, Int) = {
    (findFirstNonEmptyColumn(char), findFirstNonEmptyRow(char), findLastNonEmptyColumn(char), findLastNonEmptyRow(char))
  }

  private def findFirstNonEmptyColumn(char: Char = ' '): Int = {
    for (column <- 0 until columns()) {
      if (columnNonEmpty(column, char)) return column
    }
    0
  }

  private def findLastNonEmptyColumn(char: Char = ' '): Int = {
    val n = columns() - 1
    for (column <- n to 0 by -1) {
      if (columnNonEmpty(column, char)) return column
    }
    n
  }

  private def findFirstNonEmptyRow(char: Char = ' '): Int = {
    for (row <- 0 until rows()) {
      if (rowNonEmpty(row, char)) return row
    }
    0
  }

  private def findLastNonEmptyRow(char: Char = ' '): Int = {
    val n = rows() - 1
    for (row <- n to 0 by -1) {
      if (rowNonEmpty(row, char)) return row
    }
    n
  }

  private def getValue: Array[Array[Char]] = {
    val value = Array.ofDim[Char](height, width)
    for (y <- 0 until height) for (x <- 0 until width) value(y)(x) = this.matrix(x)(y).char
    value
  }

  def setChar(x: Int)(y: Int)(c: Char): Unit = {
    matrix(x)(y).char = c
  }

  def setFgColor(x: Int)(y: Int)(color: Color): Unit = {
    val c = color.clone()
    c.isBackground = false
    matrix(x)(y).fgColor = c
  }

  def setBgColor(x: Int)(y: Int)(color: Color): Unit = {
    val c = color.clone()
    c.isBackground = true
    matrix(x)(y).bgColor = c
  }

  def canEqual(other: Any): Boolean = other.isInstanceOf[PixelMatrix]

  override def equals(other: Any): Boolean = other match {
    case that: PixelMatrix =>
      (that canEqual this) &&
        width == that.width &&
        height == that.height &&
        fgColor == that.fgColor &&
        bgColor == that.bgColor &&
        (matrix sameElements that.matrix)
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(width, height, fgColor, bgColor, matrix)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object PixelMatrix {
  def apply(width: Int = 0,
            height: Int = 0,
            char: Char = Pixel.CHAR_DEFAULT,
            fgColor: Color = Pixel.COLOR_DEFAULT,
            bgColor: Color = Pixel.COLOR_DEFAULT,
            value: Array[Array[Char]] = Array[Array[Char]](),
            flags: Set[String] = Set(),
            offX: Int = 0,
            offY: Int = 0,
            grow: Boolean = true,
            other: Option[PixelMatrix] = Option.empty) =
    new PixelMatrix(width, height, char, fgColor, bgColor, value, flags, offX, offY, grow, other)
}
