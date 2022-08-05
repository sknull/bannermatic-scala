package de.visualdigits.bannermatic.model

import de.visualdigits.bannermatic.model.figlet.`type`.{Direction, Justify}
import de.visualdigits.bannermatic.model.pixelmatrix.`type`.Inset
import de.visualdigits.bannermatic.model.pixelmatrix.{Color, PixelMatrix}
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatestplus.junit.JUnitRunner

import java.io.File

@RunWith(classOf[JUnitRunner])
class PixelMatrixTest extends FunSuite {

  test("bg color") {
    val width = 7
    val height = 3
    val pm = PixelMatrix(width = width, height = height, char = " ")
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
    assertEquals(expectedPM, pm.toString)
  }

  test("padding and clipping") {
    var pm = PixelMatrix(width = 10, height = 5, char="_", offX=1, offY=2)
    pm = pm.pad(location = Inset.top, amount = 3, char = "1")
    pm = pm.pad(location = Inset.right, amount = 6, char = "2")
    pm = pm.pad(location = Inset.bottom, amount = 3, char = "3")
    pm = pm.pad(location = Inset.left, amount = 6, char = "4")
    pm = pm.trim(Inset.top, 2)
    pm = pm.trim(Inset.right, 4)
    pm = pm.trim(Inset.bottom, 2)
    pm = pm.trim(Inset.left, 4)

    val expectedPM = """[49m[49m44111111111122[0m
                       |[49m[49m44__________22[0m
                       |[49m[49m44__________22[0m
                       |[49m[49m44__________22[0m
                       |[49m[49m44__________22[0m
                       |[49m[49m44__________22[0m
                       |[49m[49m44333333333333[0m
                       |""".stripMargin
    assertEquals(expectedPM, pm.toString)
  }

  test("clipping") {
    val pm = PixelMatrix(width = 10, height = 2, char=" ", offX=1, offY=2, fgColor = Color.YELLOW, bgColor = Color.BLUE)
      .clip()
      .inset(1)

    val expectedPM = """[33m[44m[33m[44m              [0m
                       |[33m[44m[33m[44m              [0m
                       |[33m[44m[33m[44m              [0m
                       |[33m[44m[33m[44m              [0m
                       |""".stripMargin
    assertEquals(expectedPM, pm.toString)
  }
}
