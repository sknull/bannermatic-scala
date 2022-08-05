package de.visualdigits.bannermatic

import java.io.File

import org.scalatest.FunSuite

class Demo extends FunSuite {

  test("utf-8") {
    val c = "\uD83D\uDE05"
    println(c)
  }

  ignore("create raspberry banner") {
    val image: Option[File] = Some(new File(ClassLoader.getSystemClassLoader.getResource("images/raspberry/RaspberryPi_Logo_30.png").toURI))
    val config = Config(
      image = image,
      text = "Webserver",
      imageWidth = 40,
      textPlacement = "outside",
      valign = "bottom",
      color = "#d01349",
    )
    val banner = BannerMatic.render(config).get
    println(banner)
  }
}
