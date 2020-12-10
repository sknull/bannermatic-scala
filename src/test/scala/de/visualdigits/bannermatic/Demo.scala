package de.visualdigits.bannermatic

import java.io.File

import org.scalatest.FunSuite

class Demo extends FunSuite {

  test("create raspberry banner") {
    val image: Option[File] = Some(new File(ClassLoader.getSystemClassLoader.getResource("images/raspberry/RaspberryPi_Logo_30.png").toURI))
    val config = Config(
      image = image,
      text = "Webserver",
      imageWidth = 40,
      textPlacement = "outside",
      valign = "bottom",
      color = "#d01349",
      outputFile = Some(new File(System.getProperty("user.home"), "banner.txt"))
    )
    val banner = BannerMatic.render(config).get
  }
}
