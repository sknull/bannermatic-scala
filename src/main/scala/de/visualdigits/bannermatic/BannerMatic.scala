package de.visualdigits.bannermatic

import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.nio.file.Files

import de.visualdigits.bannermatic.model.figlet.Figlet
import de.visualdigits.bannermatic.model.figlet.`type`.{Direction, Justify}
import de.visualdigits.bannermatic.model.pixelmatrix.`type`.{Align, Placement, VAlign}
import de.visualdigits.bannermatic.model.pixelmatrix.{Color, PixelMatrix}
import javax.imageio.ImageIO

object BannerMatic {

  private val THRESHOLD_ALPHA = 4


  def renderText(text: String, width: Int = 80, font: String = "basic", fgColor: Color = Color.DEFAULT, bgColor: Color = Color.DEFAULT, direction: Direction = Direction.auto, justify: Justify = Justify.auto): PixelMatrix = {
    val figlet = Figlet(font, width, direction, justify)
    val raw = figlet.renderText(text)
    val rows = raw.split("\n").map(_.toCharArray)
    val finalWidth = Math.max(width, rows.map(_.length).max)
    PixelMatrix(finalWidth, rows.length, ' ', fgColor, bgColor, value = rows)
  }

  def renderImage(imageFile: File, width: Int, char: Char = ' ', isBackground: Boolean = true, pixelRatio: Double = 0.5): PixelMatrix = {
    val image = ImageIO.read(imageFile)
    val ratio = pixelRatio * image.getHeight / image.getWidth
    val height = (width * ratio * 10 + 0.5).toInt / 10
    val ims = scaleImage(image, width, height)
    val cm = ims.getColorModel
    val matrix = PixelMatrix(width, height, char)
    for (y <- 0 until height) for (x <- 0 until width) {
      val c = new java.awt.Color(ims.getRGB(x, y), cm.hasAlpha)
      val alpha = c.getAlpha
      val pixelColor = if (alpha < THRESHOLD_ALPHA) Color("default") else Color(red   = c.getRed, green = c.getGreen, blue = c.getBlue, alpha = alpha)
      if (isBackground) matrix.setBgColor(x)(y)(pixelColor)
      else matrix.setFgColor(x)(y)(pixelColor)
    }
    matrix
  }

  def render(config: Config): Option[PixelMatrix] = {
    var textBanner: Option[PixelMatrix] = Option.empty
    var imageBanner: Option[PixelMatrix] = Option.empty
    if (config.text.nonEmpty) {
      textBanner = Some(renderText(config.text, config.textWidth, config.font, Color(config.color), Color.DEFAULT, Direction.valueOf(config.textDirection), Justify.valueOf(config.textJustify)))
    }
    if (config.image.nonEmpty) {
      imageBanner = Some(renderImage(config.image.get, config.imageWidth))
    }
    var w = 0
    var h = 0
    if (textBanner.nonEmpty) {
      val tb = textBanner.get
      textBanner = Some(tb.clip())
      if (config.textPadding > 0) {
        textBanner = Some(tb.inset(config.textPadding))
      }
      w = Math.max(w, tb.width)
      h = Math.max(h, tb.height)
    }
    if (imageBanner.nonEmpty) {
      w = Math.max(w, imageBanner.get.width)
      h = Math.max(h, imageBanner.get.height)
    }

    val banner: Option[PixelMatrix] = if (imageBanner.nonEmpty && textBanner.nonEmpty) {
      val align = Align.valueOf(config.align)
      val valign = VAlign.valueOf(config.valign)
      val placement = Placement.valueOf(config.textPlacement)
      Some(imageBanner.get.overlay(align, valign, textBanner.get, placement))
    } else if (imageBanner.nonEmpty) imageBanner
    else if (textBanner.nonEmpty) textBanner
    else Option.empty
    if (config.outputFile.nonEmpty && banner.nonEmpty) Files.write(config.outputFile.get.toPath, banner.get.toString.getBytes())
    banner
  }

  private def scaleImage(image: BufferedImage, width: Int, height: Int): BufferedImage = {
    val resized = new BufferedImage(width, height, image.getType)
    val g = resized.createGraphics
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
    g.drawImage(image, 0, 0, width, height, 0, 0, image.getWidth, image.getHeight, null)
    g.dispose()
    resized
  }

  def main(args: Array[String]): Unit = {
    Config.parseArgs(args) match {
      case Some(config) =>
        println(config)
        val banner = render(config).map(_.toString).getOrElse("")
        if (config.outputFile.isEmpty && banner.nonEmpty) println(banner)
      case _ =>
    }
  }
}
