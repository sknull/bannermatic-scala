package de.visualdigits.bannermatic

import de.visualdigits.bannermatic.model.figlet.`type`.{Direction, Justify}
import de.visualdigits.bannermatic.model.pixelmatrix.PixelMatrix.{renderImage, renderText}
import de.visualdigits.bannermatic.model.pixelmatrix.{Color, PixelMatrix}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatestplus.junit.JUnitRunner

import java.io.File
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}


@RunWith(classOf[JUnitRunner])
class Demo extends FunSuite {

  test("create raspberry banner") {
    val imageMatrix = renderImage(
      imageFile = new File(ClassLoader.getSystemClassLoader.getResource("images/qnap-logo.png").toURI),
      width = 90,
      char = " ",
      isBackground = true,
      pixelRatio = 0.4,
//      asciiArtChars = PixelMatrix.ASCII_ART_CHARS_MEDIUM,
            asciiArtChars = Array(),
      grayscale = false,
      edgeDetection = true
    )
    val textMatrix = renderText(
      text = "fileserver",
      width = 90,
      font = "basic",
      fgColor = Color("#f7393f"),
      bgColor = Color.DEFAULT,
      direction = Direction.auto,
      justify = Justify.center
    )

    val banner = imageMatrix.toString + "\n" + textMatrix.toString
//    println(banner)
//    Files.write(Paths.get("C:\\Users\\sknull\\banner\\banner.txt"), banner.getBytes(StandardCharsets.UTF_8))
  }

  test("demo") {
    val image: Option[File] = Some(new File(ClassLoader.getSystemClassLoader.getResource("images/raspberry/RaspberryPi_Logo_30.png").toURI))
    val config = Config(
      image = image,
      text = "fileserver",
      imageWidth = 120,
      textPlacement = "outside",
      valign = "bottom",
      color = "#d01349",
      textJustify = Justify.left.entryName
    )
    val banner = PixelMatrix(config)
//    println(banner)
  }
}
