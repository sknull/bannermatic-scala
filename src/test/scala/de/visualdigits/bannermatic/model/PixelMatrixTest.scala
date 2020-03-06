package de.visualdigits.bannermatic.model

import de.visualdigits.bannermatic.model.pixelmatrix.`type`.Inset
import de.visualdigits.bannermatic.model.pixelmatrix.{Color, PixelMatrix}
import org.junit.Assert
import org.scalatest.FunSuite

class PixelMatrixTest extends FunSuite {

  test("bg color") {
    val width = 7
    val height = 3
    val pm = PixelMatrix(width, height, ' ')
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        val c2 = Color(red = (255 * Math.random()).toInt, green = (255 * Math.random()).toInt, blue = (255 * Math.random()).toInt)
        pm.setBgColor(x)(y)(Color.WHITE)
      }
    }

    val expectedPM = """[49m[49m[49m[48;2;255;255;255m       [0m
                       |[49m[49m[49m[48;2;255;255;255m       [0m
                       |[49m[49m[49m[48;2;255;255;255m       [0m
                       |""".stripMargin
    Assert.assertEquals(expectedPM, pm.toString)
  }

  test("padding and clipping") {
    var pm = PixelMatrix(10, 5, value = Array[Array[Char]]("Hello".toCharArray, "World".toCharArray), char='_', offX=1, offY=2)
    pm = pm.pad(location = Inset.top, amount = 3, char = '1')
    pm = pm.pad(location = Inset.right, amount = 6, char = '2')
    pm = pm.pad(location = Inset.bottom, amount = 3, char = '3')
    pm = pm.pad(location = Inset.left, amount = 6, char = '4')
    pm = pm.trim(Inset.top, 2)
    pm = pm.trim(Inset.right, 4)
    pm = pm.trim(Inset.bottom, 2)
    pm = pm.trim(Inset.left, 4)

    val expectedPM = """[49m[49m44111111111122[0m
                       |[49m[49m44__________22[0m
                       |[49m[49m44__________22[0m
                       |[49m[49m44_Hello____22[0m
                       |[49m[49m44_World____22[0m
                       |[49m[49m44__________22[0m
                       |[49m[49m44333333333333[0m
                       |""".stripMargin
    Assert.assertEquals(expectedPM, pm.toString)
  }

  test("clipping") {
    val pm = PixelMatrix(10, 5, value = Array[Array[Char]]("Hello".toCharArray, "World".toCharArray), char=' ', offX=1, offY=2, fgColor = Color.YELLOW, bgColor = Color.BLUE)
      .clip()
      .inset(1)

    val expectedPM = """[33m[44m[33m[44m         [0m
                       |[33m[44m[33m[44m  Hello  [0m
                       |[33m[44m[33m[44m  World  [0m
                       |[33m[44m[33m[44m         [0m
                       |""".stripMargin
    Assert.assertEquals(expectedPM, pm.toString)
  }
}
