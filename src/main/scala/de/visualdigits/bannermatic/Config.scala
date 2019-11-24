package de.visualdigits.bannermatic

import java.io.File

import de.visualdigits.bannermatic.model.figlet.`type`.{Direction, Justify}
import de.visualdigits.bannermatic.model.pixelmatrix.`type`.{Align, Placement, VAlign}
import scopt.OParser


case class Config(
                   text: String = "",
                   textWidth: Int = 80,
                   textPadding: Int = 1,
                   textDirection: String = Direction.auto.entryName,
                   textJustify: String = Justify.auto.entryName,
                   textPlacement: String = Placement.inside.entryName,
                   color: String = "default",
                   font: String = "basic",
                   image: Option[File] = Option.empty,
                   imageWidth: Int = 80,
                   align: String = Align.center.entryName,
                   valign: String = VAlign.middle.entryName,
                   outputFile: Option[File] = Option.empty,
                   //                   margin: String = "",
                   //                   inverse: Boolean = false,
                   //                   clip: Boolean = true
                 )

object Config extends scala.AnyRef {
  private val builder = OParser.builder[Config]
  private val parser = {
    import builder._
    OParser.sequence(
      programName("bannermatic"),
      head("bannermatic", "1.0.0-SNAPSHOT"),
      opt[String]('c', "color")
        .action((x, c) => c.copy(color = x))
        .text("""prints text with passed foreground color,
                           --color=foreground:background\t
                           --color=:background\t\t\t# only background
                           --color=foreground | foreground:\t# only foreground
                           --color=list\t\t\t# list all colors\t
                           COLOR = list[COLOR] | [0-255];[0-255];[0-255] (RGB)"""),
      opt[String]('f', "font")
        .action((x, c) => c.copy(font = x))
        .text("font to render with (default: %default)"),
      opt[Int]('w', "text-width")
        .action((x, c) => c.copy(textWidth = x))
        .text("set text width in columns"),
      opt[Int]('P', "text-padding")
        .action((x, c) => c.copy(textPadding = x))
        .text("set padding of the text inside its box"),
      opt[String]('t', "text")
        .action((x, c) => c.copy(text = x))
        .text("The text"),
      opt[String]('a', "horizontal-align")
        .action((x, c) => c.copy(align = x))
        .text("set horizontal align, one of: left, center, right"),
      opt[String]('v', "vertical-align")
        .action((x, c) => c.copy(valign = x))
        .text("set horizontal align, one of: left, center, right"),
      opt[String]('d', "text-direction")
        .action((x, c) => c.copy(textDirection = x))
        .text("set direction of text, one of: auto, left_to_right, right_to_left"),
      opt[String]('j', "text-justify")
        .action((x, c) => c.copy(textJustify = x))
        .text("set text justify, one of: auto, left, center, right"),
      opt[String]('p', "text-placement")
        .action((x, c) => c.copy(textPlacement = x))
        .text("set text placement, one of: inside, outside"),
//      opt[String]('M', "margin")
//        .action((x, c) => c.copy(margin = x))
//        .text("set margin between image and text in columns"),
      opt[File]('i', "image")
        .action((x, c) => c.copy(image = Some(x)))
        .text("The image"),
      opt[Int]('W', "image-width")
        .action((x, c) => c.copy(imageWidth = x))
        .text("set image width in columns"),
      opt[File]('o', "output-file")
        .action((x, c) => c.copy(outputFile = Some(x)))
        .text("The target file"),
//      opt[Boolean]('i', "inverse")
//        .action((x, c) => c.copy(inverse = x))
//        .text("invert output"),
//      opt[Boolean]("clip")
//        .action((x, c) => c.copy(clip = x))
//        .text("clip to size of overlay after composing")
    )
  }

  def parseArgs(args: Array[String]): Option[Config] = {
    OParser.parse(parser, args, Config())
  }
}