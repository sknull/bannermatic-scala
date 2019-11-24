package de.visualdigits.bannermatic.model

import de.visualdigits.bannermatic.model.pixelmatrix.`type`.Inset
import de.visualdigits.bannermatic.model.pixelmatrix.{Color, PixelMatrix}
import org.junit.Test
import org.scalatest.FunSuite

class PixelMatrixTest {

  @Test def testAspect(): Unit = {
    val width = 7
    val height = 3
    val pm = PixelMatrix(width, height, ' ')
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        val c2 = Color(red = (255 * Math.random()).toInt, green = (255 * Math.random()).toInt, blue = (255 * Math.random()).toInt)
        pm.setBgColor(x)(y)(Color.WHITE)
      }
    }
    println(pm)
  }

  @Test def testPad(): Unit = {
    var pm = PixelMatrix(10, 5, value = Array[Array[Char]]("Hello".toCharArray, "World".toCharArray), char='_', offX=1, offY=2)
    pm.char = '1'
    pm = pm.pad(Inset.top, 3)
    pm.char = '2'
    pm = pm.pad(Inset.right, 6)
    pm.char = '3'
    pm = pm.pad(Inset.bottom, 3)
    pm.char = '4'
    pm = pm.pad(Inset.left, 6)
    pm = pm.trim(Inset.top, 2)
    pm = pm.trim(Inset.right, 4)
    pm = pm.trim(Inset.bottom, 2)
    pm = pm.trim(Inset.left, 4)
    println(pm)
  }

  @Test def testClip(): Unit = {
    var pm = PixelMatrix(10, 5, value = Array[Array[Char]]("Hello".toCharArray, "World".toCharArray), char=' ', offX=1, offY=2, fgColor = Color.YELLOW, bgColor = Color.BLUE)
      .clip()
      .inset(1)
    println(pm)
  }
}
