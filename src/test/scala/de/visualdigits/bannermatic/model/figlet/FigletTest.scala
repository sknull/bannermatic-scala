package de.visualdigits.bannermatic.model.figlet

import org.junit.{Assert, Test}
import org.scalatest.FunSuite

class FigletTest extends FunSuite {

  test("test") {
    val figlet = Figlet(font = "5lineoblique", width = 100)
    val text = figlet.renderText("Hello World!")
    val expectedText = "                                                                                     \n                                                                                     \n    //    / /                         ||   / |  / /                               // \n   //___ / /  ___     // //  ___      ||  /  | / /  ___      __     //  ___   /  //  \n  / ___   / //___) ) // // //   ) )   || / /||/ / //   ) ) //  ) ) // //   ) /  //   \n //    / / //       // // //   / /    ||/ / |  / //   / / //      // //   / /        \n//    / / ((____   // // ((___/ /     |  /  | / ((___/ / //      // ((___/ /  //     \n"
    Assert.assertEquals(expectedText, text.toString)
  }
}
