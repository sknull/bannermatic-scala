package de.visualdigits.bannermatic.model.pixelmatrix

import de.visualdigits.bannermatic.Config
import de.visualdigits.bannermatic.model.figlet.Figlet
import de.visualdigits.bannermatic.model.figlet.`type`.{Direction, Justify}
import de.visualdigits.bannermatic.model.pixelmatrix.`type`.{Align, Inset, Placement, VAlign}
import de.visualdigits.bannermatic.utils.StringUtil.stringOption

import java.awt
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files
import javax.imageio.ImageIO
import scala.collection.mutable

case class PixelMatrix(
                        var width: Int = 0,
                        var height: Int = 0,
                        var char: String = Pixel.CHAR_DEFAULT,
                        var fgColor: Color = Pixel.COLOR_DEFAULT,
                        var bgColor: Color = Pixel.COLOR_DEFAULT,
                        asciiArtChars: Array[String] = Array(),
                        grayscale: Boolean = false,
                        edgeDetection: Boolean = false,

                        value: Array[Array[String]] = Array[Array[String]](),
                        flags: Set[String] = Set(),
                        offX: Int = 0,
                        offY: Int = 0,
                        grow: Boolean = true,
                        other: Option[PixelMatrix] = Option.empty
                      ) {

  private val THRESHOLD_ALPHA = 4

  private var matrix: Array[Array[Pixel]] = Array[Array[Pixel]]()

  private val nAsciiArtChars: Int = asciiArtChars.length

  initMatrix(width, height)

  if (value.nonEmpty) {
    loadRows(value, offX, offY, grow, nAsciiArtChars > 0)
  } else if (other.nonEmpty) {
    val o = other.get
    loadRows(o.getValue, offX, offY, grow, nAsciiArtChars > 0)
    for (y <- 0 until o.height) {
      for (x <- 0 until o.width) {
        val op = o.matrix(x)(y)
        setFgColor(x + offX)(y + offY)(op.fgColor)
        setBgColor(x + offX)(y + offY)(op.bgColor)
      }
    }
  }

  override def clone(): PixelMatrix = {
    val c = PixelMatrix(width = width, height = height, char = char, fgColor = fgColor, bgColor = bgColor, value = getValue, flags = flags, asciiArtChars = asciiArtChars, grayscale = grayscale, edgeDetection = edgeDetection)
    for (y <- 0 until height) for (x <- 0 until width) c.matrix(x)(y) = matrix(x)(y).clone()
    c
  }

  private def initMatrix(width: Int = 0, height: Int = 0): PixelMatrix = {
    val isAsciiArt = nAsciiArtChars > 0
    this.width = width
    this.height = height
    this.matrix = Array.ofDim[Pixel](width, height)
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        this.matrix(x)(y) = Pixel(fgColor, bgColor, char, isAsciiArt, grayscale)
      }
    }
    this
  }

  private def loadRows(value: Array[Array[String]], offX: Int, offY: Int, grow: Boolean, isAsciiArt: Boolean): Unit = {
    val w = value.map(_.length).max
    this.width = if (grow) Math.max(width, w) else Math.min(width, w)
    val h = value.length
    this.height = if (grow) Math.max(height, h) else Math.min(height, h)
    this.matrix = Array.ofDim[Pixel](width, height)
    for (y <- 0 until this.height) {
      for (x <- 0 until this.width) {
        this.matrix(x)(y) = Pixel(fgColor, bgColor, char, isAsciiArt, grayscale)
      }
    }
    if (grow) {
      for (y <- 0 until h) {
        val row = value(y)
        val w = row.length
        for (x <- 0 until w) {
          this.matrix(offX + x)(offY + y) = Pixel(fgColor, bgColor, row(x), isAsciiArt, grayscale)
        }
      }
    } else {
      for (y <- 0 until this.height) {
        for (x <- 0 until this.width) {
          this.matrix(x)(y) = Pixel(fgColor, bgColor, value(y + offY)(x + offX), isAsciiArt, grayscale)
        }
      }
    }
  }

  override def toString: String = {
    val rows = mutable.ListBuffer[String]()
    val prefix = if (grayscale && nAsciiArtChars > 0) {
      ""
    } else {
      fgColor.toString + bgColor.toString + flags.mkString("")
    }
    val suffix = if (grayscale && nAsciiArtChars > 0) {
      ""
    } else {
      PixelMatrix.RESET
    }
    for (y <- 0 until height) {
      var row = prefix
      var prevPixel = Pixel()
      for (x <- 0 until width) {
        val pixel = matrix(x)(y)
        if (!pixel.hasSameColors(prevPixel)) {
          row += pixel.toString
          prevPixel = pixel
        }
        else {
          row += pixel.char
        }
      }
      rows += row
    }
    rows.mkString(suffix + "\n") + suffix + "\n"
  }

  def loadImage(image: BufferedImage, width: Int, height: Int, isBackground: Boolean, grayscale: Boolean): PixelMatrix = {
    val imageScaled = scaleImage(image, width, height)
    val colorModel = imageScaled.getColorModel
    for (y <- 0 until height) for (x <- 0 until width) {
      val c = new awt.Color(imageScaled.getRGB(x, y), colorModel.hasAlpha)
      val alpha = c.getAlpha
      val pixelIsNearBlack = Seq(c.getRed, c.getGreen, c.getBlue).forall(_ < THRESHOLD_ALPHA)
      val pixelIsNearTransparent = colorModel.hasAlpha && c.getAlpha < THRESHOLD_ALPHA
      val pixelColor = if (pixelIsNearTransparent || pixelIsNearBlack) {
        Color("default")
      } else {
        var color = Color(red = c.getRed, green = c.getGreen, blue = c.getBlue, alpha = alpha).fade(bgColor)
        if (grayscale) {
          val gray = (color.toGray * 255).toInt
          color = Color(red = gray, green = gray, blue = gray, alpha = alpha)
        }
        color
      }
      if (nAsciiArtChars == 0) {
        if (isBackground) {
          setBgColor(x)(y)(pixelColor)
        } else {
          setFgColor(x)(y)(pixelColor)
        }
      } else {
        if (!grayscale) {
          setFgColor(x)(y)(pixelColor)
        }
        setChar(x)(y)(asciiArtChars((pixelColor.toGray * nAsciiArtChars).toInt))
      }
    }
    this
  }

  private def scaleImage(image: BufferedImage, width: Int, height: Int): BufferedImage = {
    val resized = new BufferedImage(width, height, image.getType)
    val g = resized.createGraphics
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth, image.getHeight, null)
    g.dispose()
    resized
  }

  def pad(location: Inset, amount: Int, char: String = " "): PixelMatrix = {
    location match {
      case Inset.top => PixelMatrix(width = width, height = height + amount, char = char, fgColor = fgColor, bgColor = bgColor, flags = flags, offY = amount, other = Some(this), asciiArtChars = asciiArtChars, grayscale = grayscale, edgeDetection = edgeDetection)
      case Inset.bottom => PixelMatrix(width = width, height = height + amount, char = char, fgColor = fgColor, bgColor = bgColor, flags = flags, other = Some(this), asciiArtChars = asciiArtChars, grayscale = grayscale, edgeDetection = edgeDetection)
      case Inset.left => PixelMatrix(width = width + amount, height = height, char = char, fgColor = fgColor, bgColor = bgColor, flags = flags, offX = amount, other = Some(this), asciiArtChars = asciiArtChars, grayscale = grayscale, edgeDetection = edgeDetection)
      case Inset.right => PixelMatrix(width = width + amount, height = height, char = char, fgColor = fgColor, bgColor = bgColor, flags = flags, other = Some(this), asciiArtChars = asciiArtChars, grayscale = grayscale, edgeDetection = edgeDetection)
      case _ => clone()
    }
  }

  def trim(location: Inset, amount: Int): PixelMatrix = {
    location match {
      case Inset.top => PixelMatrix(width = width, height = height - amount, char = char, fgColor = fgColor, bgColor = bgColor, value = getValue, flags = flags, offY = amount, grow = false, asciiArtChars = asciiArtChars, grayscale = grayscale, edgeDetection = edgeDetection)
      case Inset.bottom => PixelMatrix(width = width, height = height - amount, char = char, fgColor = fgColor, bgColor = bgColor, value = getValue, flags = flags, grow = false, asciiArtChars = asciiArtChars, grayscale = grayscale, edgeDetection = edgeDetection)
      case Inset.left => PixelMatrix(width = width - amount, height = height, char = char, fgColor = fgColor, bgColor = bgColor, value = getValue, flags = flags, offX = amount, grow = false, asciiArtChars = asciiArtChars, grayscale = grayscale, edgeDetection = edgeDetection)
      case Inset.right => PixelMatrix(width = width - amount, height = height, char = char, fgColor = fgColor, bgColor = bgColor, value = getValue, flags = flags, grow = false, asciiArtChars = asciiArtChars, grayscale = grayscale, edgeDetection = edgeDetection)
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

  def rowEmpty(row: Int, char: String = " "): Boolean = {
    matrix.forall(_(row).char == char)
  }

  def rowNonEmpty(row: Int, char: String = " "): Boolean = {
    matrix.exists(_(row).char != char)
  }

  def columnEmpty(column: Int, char: String = " "): Boolean = {
    matrix(column).forall(_.char == char)
  }

  def columnNonEmpty(column: Int, char: String = " "): Boolean = {
    matrix(column).exists(_.char != char)
  }

  def inset(amount: Int, pixelRatio: Double = 0.5, char: String = " "): PixelMatrix = {
    clone()
      .pad(Inset.top, amount, char)
      .pad(Inset.bottom, amount, char)
      .pad(Inset.left, (amount / pixelRatio).toInt, char)
      .pad(Inset.right, (amount / pixelRatio).toInt, char)
  }

  def clip(char: String = " "): PixelMatrix = {
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

  def boundingBox(char: String = " "): (Int, Int, Int, Int) = {
    (findFirstNonEmptyColumn(char), findFirstNonEmptyRow(char), findLastNonEmptyColumn(char), findLastNonEmptyRow(char))
  }

  private def findFirstNonEmptyColumn(char: String = " "): Int = {
    for (column <- 0 until columns()) {
      if (columnNonEmpty(column, char)) return column
    }
    0
  }

  private def findLastNonEmptyColumn(char: String = " "): Int = {
    val n = columns() - 1
    for (column <- n to 0 by -1) {
      if (columnNonEmpty(column, char)) return column
    }
    n
  }

  private def findFirstNonEmptyRow(char: String = " "): Int = {
    for (row <- 0 until rows()) {
      if (rowNonEmpty(row, char)) return row
    }
    0
  }

  private def findLastNonEmptyRow(char: String = " "): Int = {
    val n = rows() - 1
    for (row <- n to 0 by -1) {
      if (rowNonEmpty(row, char)) return row
    }
    n
  }

  private def getValue: Array[Array[String]] = {
    val value = Array.ofDim[String](height, width)
    for (y <- 0 until height) for (x <- 0 until width) value(y)(x) = this.matrix(x)(y).char
    value
  }

  def setChar(x: Int)(y: Int)(c: String): Unit = {
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
        asciiArtChars == that.asciiArtChars &&
        grayscale == that.grayscale &&
        (matrix sameElements that.matrix)
    case _ => false
  }

  override def hashCode(): Int = {
    val state = Seq(width, height, fgColor, bgColor, matrix)
    state.map(_.hashCode()).foldLeft(0)((a, b) => 31 * a + b)
  }
}

object PixelMatrix {

  val ESCAPE: Char = BigInt("033", 8).toChar

  val CODE_FG = 38

  val RESET: String = PixelMatrix.ESCAPE + "[0m"

  val FLAG_BLINK_SLOW: String = PixelMatrix.ESCAPE + "[5m"
  val FLAG_BLINK_RAPID: String = PixelMatrix.ESCAPE + "[6m"
  val FLAG_BLINK_OFF: String = PixelMatrix.ESCAPE + "[25m"
  val FLAG_INVERSE: String = PixelMatrix.ESCAPE + "[7m"

  val ASCII_ART_CHARS_DEFAULT: Array[String] = "$@B%8&WM#*oahkbdpqwmZO0QLCJUYXzcvunxrjft/\\|()1{}[]?-_+~<>i!lI;:,\"^`'. ".reverse.toCharArray.map(_.toString)
  val ASCII_ART_CHARS_MEDIUM: Array[String] = "︎@#MOI$&%*+=-:. ".reverse.toCharArray.map(_.toString)
  val ASCII_ART_CHARS_PERL: Array[String] = " .,:;+=oaeOAM#$@".toCharArray.map(_.toString)
  val ASCII_ART_CHARS_SHORT: Array[String] = "@%#*+=-:. ".reverse.toCharArray.map(_.toString)
  val ASCII_ART_CHARS_SMILEYS: Array[String] = Array(" ", "🫥", "〠", "👌", "👐", "+", "*", "%", "&", "〠", "I", "O", "M", "#", "😍")
""
  def renderImage(
             imageFile: File,
             width: Int,
             char: String,
             isBackground: Boolean,
             pixelRatio: Double,
             asciiArtChars: Array[String],
             grayscale: Boolean,
             edgeDetection: Boolean
           ): PixelMatrix = {
    val image = ImageIO.read(imageFile)
    val ratio = pixelRatio * image.getHeight / image.getWidth
    val height = (width * ratio * 10 + 0.5).toInt / 10
    PixelMatrix(width = width, height = height, char = char, asciiArtChars = asciiArtChars, grayscale = grayscale, edgeDetection = edgeDetection)
      .loadImage(image, width, height, isBackground, grayscale)
  }

  def renderText(
             text: String,
             width: Int,
             font: String,
             fgColor: Color,
             bgColor: Color,
             direction: Direction,
             justify: Justify
           ): PixelMatrix = {
    val figlet = Figlet(font, width, direction, justify)
    val raw = figlet.renderText(text)
    val rows = raw.split("\n").map(_.toCharArray.map(_.toString))
    val finalWidth = Math.max(width, rows.map(_.length).max)
    PixelMatrix(width = finalWidth, height = rows.length, fgColor = fgColor, bgColor = bgColor, value = rows)
  }

  def apply(config: Config): PixelMatrix = {
    var textBanner = stringOption(config.text).map(text =>
      renderText(
        text = text,
        width = config.textWidth,
        font = config.font,
        fgColor = Color(config.color),
        bgColor = Color.DEFAULT,
        direction = Direction.valueOf(config.textDirection),
        justify = Justify.valueOf(config.textJustify)
      ))
    val imageBanner = config.image.map(image => renderImage(
      imageFile = image,
      width = config.imageWidth,
      char = " ",
      isBackground = true,
      pixelRatio = config.pixelRatio,
      asciiArtChars = config.asciiArtChars,
      grayscale = config.grayscale,
      edgeDetection = config.edgeDetection
    ))
    var w = 0
    var h = 0
    if (textBanner.nonEmpty) {
      val tb = textBanner.get
      textBanner = Some(tb.clip())
      if (config.textPadding > 0) {
        textBanner = Some(tb.inset(config.textPadding, config.pixelRatio))
      }
      w = Math.max(w, tb.width)
      h = Math.max(h, tb.height)
    }
    if (imageBanner.nonEmpty) {
      w = Math.max(w, imageBanner.get.width)
      h = Math.max(h, imageBanner.get.height)
    }

    val banner = {
      if (imageBanner.nonEmpty && textBanner.nonEmpty) {
        val align = Align.valueOf(config.align)
        val valign = VAlign.valueOf(config.valign)
        val placement = Placement.valueOf(config.textPlacement)
        Some(imageBanner.get.overlay(align, valign, textBanner.get, placement))
      } else if (imageBanner.nonEmpty) {
        imageBanner
      } else if (textBanner.nonEmpty) {
        textBanner
      } else {
        None
      }
    }
    config.outputFile.foreach(of => banner.foreach(b => Files.write(of.toPath, b.toString.getBytes())))
    banner.orNull
  }
}