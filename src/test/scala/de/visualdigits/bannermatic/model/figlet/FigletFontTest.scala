package de.visualdigits.bannermatic.model.figlet

import org.junit.{Assert, Test}
import org.scalatest.FunSuite

class FigletFontTest extends FunSuite {

  test("test") {
    val font = FigletFont("fonts/brite.flf")
    Assert.assertEquals(11, font.height)
    Assert.assertEquals(8, font.baseLine)
  }
}
