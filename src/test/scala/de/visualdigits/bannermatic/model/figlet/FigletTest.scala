package de.visualdigits.bannermatic.model.figlet

import org.junit.Assert
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class FigletTest extends FunSuite {

  test("test") {
    val figlet = Figlet(fontName = "5lineoblique", width = 100)
    val text = figlet.renderText("Hello World!")
    val expectedText = "                                                                                     \n                                                                                     \n    //    / /                         ||   / |  / /                               // \n   //___ / /  ___     // //  ___      ||  /  | / /  ___      __     //  ___   /  //  \n  / ___   / //___) ) // // //   ) )   || / /||/ / //   ) ) //  ) ) // //   ) /  //   \n //    / / //       // // //   / /    ||/ / |  / //   / / //      // //   / /        \n//    / / ((____   // // ((___/ /     |  /  | / ((___/ / //      // ((___/ /  //     \n"
    Assert.assertEquals(expectedText, text.toString)
  }
}
