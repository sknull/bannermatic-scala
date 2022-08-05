package de.visualdigits.bannermatic.model.figlet

import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FigletFontTest extends FunSuite {

  test("test") {
    val font = FigletFont("fonts/brite.flf")
    Assert.assertEquals(11, font.height)
    Assert.assertEquals(8, font.baseLine)
  }
}
