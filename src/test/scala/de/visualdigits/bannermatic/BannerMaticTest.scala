package de.visualdigits.bannermatic

import java.io.File

import de.visualdigits.bannermatic.model.pixelmatrix.`type`.{Align, Placement, VAlign}
import de.visualdigits.bannermatic.model.pixelmatrix.Color
import org.junit.Test

class BannerMaticTest {

  @Test
  def testRenderText(): Unit = {
    val matrix = BannerMatic.renderText("Hello World!", 80, fgColor = Color.WHITE)
    println(matrix.clip().inset(1))
  }

  @Test
  def testRenderImage(): Unit = {
    val image: File = new File(ClassLoader.getSystemClassLoader.getResource("images/rose-red.png").toURI)
    val matrix = BannerMatic.renderImage(image, 120, pixelRatio = 3.0 / 7.0)
    println(matrix)
  }

  @Test
  def combineTest(): Unit = {
    val image: File = new File(ClassLoader.getSystemClassLoader.getResource("images/raspberrypi.png").toURI)
    val bg = BannerMatic.renderImage(image, 90, pixelRatio = 3.0 / 7.0)
    val fg = BannerMatic.renderText("RASPBERRY PI", 100, fgColor = Color("#bb1142"))
    val o = bg.overlay(Align.right, VAlign.middle, fg, Placement.outside)
    println(o)
  }

  @Test
  def testRender(): Unit = {
    val image: Option[File] = Some(new File(ClassLoader.getSystemClassLoader.getResource("images/rose-red.png").toURI))
    val config = Config(image = image, text = "Hello World!", textJustify = "right", textDirection = "right_to_left")
    val banner = BannerMatic.render(config)
    println(banner.get)
  }

  @Test
  def testRenderFile(): Unit = {
    val image: Option[File] = Some(new File(ClassLoader.getSystemClassLoader.getResource("images/rose-red.png").toURI))
    val config = Config(image = image, text = "Hello World!", align="left", valign="top", outputFile = Some(new File(System.getProperty("user.home"), "banner.txt")))
    BannerMatic.render(config)
  }
}
