package de.visualdigits.bannermatic.model

import de.visualdigits.bannermatic.model.pixelmatrix.{Color, PixelMatrixConstants}
import org.junit.Assert._
import org.scalatest.FunSuite


class ColorTest extends FunSuite {

  test("test colors") {
    val c1 = Color()
    val c2 = Color(red = 255)
    val c3 = Color(green = 255)
    val c4 = Color(blue = 255)
    val c5 = Color(alpha = 255)
    val c6 = Color(isBackground = true)
    val c7 = Color("blue")
    val c8 = Color("blue", true)
    val c9 = Color("255;0;0")

    assertEquals(PixelMatrixConstants.ESCAPE + "[38;2;0;0;0m"  , c1.toString())
    assertEquals(PixelMatrixConstants.ESCAPE + "[38;2;255;0;0m", c2.toString())
    assertEquals(PixelMatrixConstants.ESCAPE + "[38;2;0;255;0m", c3.toString())
    assertEquals(PixelMatrixConstants.ESCAPE + "[38;2;0;0;255m", c4.toString())
    assertEquals(PixelMatrixConstants.ESCAPE + "[48;2;0;0;0m"  , c6.toString())
    assertEquals(PixelMatrixConstants.ESCAPE + "[34m"          , c7.toString())
    assertEquals(PixelMatrixConstants.ESCAPE + "[44m"          , c8.toString())
    assertEquals(PixelMatrixConstants.ESCAPE + "[38;2;255;0;0m", c9.toString())

    assertNotEquals("Compare with None considered as equal", c1, None)
    assertNotEquals("Compare with None considered as equal", None, c1)

    assertEquals("c1 and c1 not considered equal", c1, c1)
    assertNotEquals("c1 and c2 considered equal" , c1, c2)
    assertNotEquals("c1 and c3 considered equal" , c1, c3)
    assertNotEquals("c1 and c4 considered equal" , c1, c4)
    assertNotEquals("c1 and c5 considered equal" , c1, c5)
    assertEquals("c1 and c6 not considered equal", c1, c6)
  }
}
