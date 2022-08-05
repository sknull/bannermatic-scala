package de.visualdigits.bannermatic

import de.visualdigits.bannermatic.model.figlet.`type`.{Direction, Justify}
import de.visualdigits.bannermatic.model.pixelmatrix.PixelMatrix.{renderImage, renderText}
import de.visualdigits.bannermatic.model.pixelmatrix.{Color, PixelMatrix}
import org.scalatest.FunSuite

import java.io.File

class Demo extends FunSuite {

  test("create raspberry banner") {
    val imageMatrix = renderImage(
      imageFile = new File(ClassLoader.getSystemClassLoader.getResource("images/rose-red.png").toURI),
      width = 80,
      char = " ",
      isBackground = true,
      pixelRatio = 0.4,
      asciiArtChars = PixelMatrix.ASCII_ART_CHARS_MEDIUM,
      //      asciiArtChars = Array(),
      grayscale = true,
      edgeDetection = true
    )
    val textMatrix = renderText(
      text = "RaspBerryPi",
      width = 120,
      font = "basic",
      fgColor = Color.GREEN,
      bgColor = Color.BLACK,
      direction = Direction.auto,
      justify = Justify.center
    )

    //    Files.write(Paths.get("/Users/knull/banner.txt"), matrix.toString.getBytes())
    println(textMatrix)
    println(imageMatrix)
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
    val banner = PixelMatrix(config)
    println(banner)
  }
}
