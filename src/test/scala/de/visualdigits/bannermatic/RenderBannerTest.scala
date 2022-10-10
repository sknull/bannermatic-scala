package de.visualdigits.bannermatic

import de.visualdigits.bannermatic.model.figlet.`type`.{Direction, Justify}
import de.visualdigits.bannermatic.model.pixelmatrix.PixelMatrix.{renderImage, renderText}
import de.visualdigits.bannermatic.model.pixelmatrix.`type`.{Align, Placement, VAlign}
import de.visualdigits.bannermatic.model.pixelmatrix.{Color, PixelMatrix}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatestplus.junit.JUnitRunner

import java.io.File

@RunWith(classOf[JUnitRunner])
class RenderBannerTest extends FunSuite {

  test("Render a banner") {
    val image: File = new File(ClassLoader.getSystemClassLoader.getResource("images/rose-red.png").toURI)
    val bg = renderImage(
      imageFile = image,
      width = 60,
      pixelRatio = 3.0 / 7.0,
      asciiArtChars = Array(),
      grayscale = true,
    ).orNull
    val fg = renderText(
      text = "TEXTBOX",
      width = 100,
      font = "basic",
      fgColor = Color.RED,
      bgColor = Color.DEFAULT,
      direction = Direction.auto,
      justify = Justify.center
    ).orNull
    val banner = bg.overlay(Align.center, VAlign.bottom, fg, Placement.outside)
//    IOUtils.write(banner.toString, new FileOutputStream(new File("/Users/knull/Pictures/banner.txt")))
  }
}
