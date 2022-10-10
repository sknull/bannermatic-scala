package de.visualdigits.bannermatic

import de.visualdigits.bannermatic.model.Config
import de.visualdigits.bannermatic.model.pixelmatrix.PixelMatrix

object BannerMatic {

  def main(args: Array[String]): Unit = {
    Config.parseArgs(args) match {
      case Some(config) =>
        println(config)
        val banner = PixelMatrix(config).toString
        if (config.outputFile.isEmpty && banner.nonEmpty) {
          println(banner)
        }
      case _ =>
    }
  }
}
