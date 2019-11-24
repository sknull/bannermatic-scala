package de.visualdigits.bannermatic.model.figlet

import org.junit.Test

class FigletTest {

  @Test def test(): Unit = {
    val figlet = Figlet(font = "5lineoblique", width = 100)
    val text = figlet.renderText("Hello World!")
    println(text)
  }
}
