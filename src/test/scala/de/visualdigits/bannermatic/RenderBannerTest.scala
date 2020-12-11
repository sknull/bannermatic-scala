package de.visualdigits.bannermatic

import java.io.{File, FileOutputStream}

import de.visualdigits.bannermatic.model.pixelmatrix.Color
import de.visualdigits.bannermatic.model.pixelmatrix.`type`.{Align, Placement, VAlign}
import org.apache.commons.io.IOUtils
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class RenderBannerTest extends FunSuite {

  test("Render a banner") {
    val image: File = new File(ClassLoader.getSystemClassLoader.getResource("images/Homer-Jack-In-The-Box.png").toURI)
    val bg = BannerMatic.renderImage(image, 60, pixelRatio = 3.0 / 7.0)
    val fg = BannerMatic.renderText("TEXTBOX", 100, fgColor = Color.RED)
    val banner = bg.overlay(Align.center, VAlign.bottom, fg, Placement.outside)
    IOUtils.write(banner.toString, new FileOutputStream(new File("/Users/knull/Pictures/banner.txt")))
  }
}
