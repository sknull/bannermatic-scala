package de.visualdigits.bannermatic

import de.visualdigits.bannermatic.model.figlet.`type`.{Direction, Justify}
import de.visualdigits.bannermatic.model.pixelmatrix.`type`.{Align, Placement, VAlign}
import de.visualdigits.bannermatic.model.pixelmatrix.{Color, PixelMatrix}
import de.visualdigits.bannermatic.utils.StringUtil.stringOption

import java.io.File
import java.nio.file.Files

object BannerMatic {

  def renderText(
                  text: String,
                  width: Int = 80,
                  font: String = "basic",
                  fgColor: Color = Color.DEFAULT,
                  bgColor: Color = Color.DEFAULT,
                  direction: Direction = Direction.auto,
                  justify: Justify = Justify.auto
                ): PixelMatrix = {
    PixelMatrix(
      text = text,
      width = width,
      font = font,
      fgColor = fgColor,
      bgColor = bgColor,
      direction = direction,
      justify = justify
    )
  }

  def renderImage(
                   imageFile: File,
                   width: Int,
                   char: Char = ' ',
                   isBackground: Boolean = true,
                   pixelRatio: Double = 0.5
                 ): PixelMatrix = {
    PixelMatrix(
      imageFile = imageFile,
      width = width,
      char = char,
      isBackground = isBackground,
      pixelRatio = pixelRatio,
      asciiArtChars = "",
      grayscale = false,
      edgeDetection = false
    )
  }

  def render(config: Config): Option[PixelMatrix] = {
    var textBanner = stringOption(config.text).map(text => PixelMatrix(text = text, width = config.textWidth, font = config.font, fgColor = Color(config.color), bgColor = Color.DEFAULT, direction = Direction.valueOf(config.textDirection), justify = Justify.valueOf(config.textJustify)))
    val imageBanner = config.image.map(image => PixelMatrix(
      imageFile = image,
      width = config.imageWidth,
      char = ' ',
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
    banner
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
