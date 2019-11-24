package de.visualdigits.bannermatic.model.figlet

import org.junit.Test

class FigletFontTest {

  @Test def test(): Unit = {
    val font = FigletFont("fonts/brite.flf")
    println(font)
  }
}
