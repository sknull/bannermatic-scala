package de.visualdigits.bannermatic.model.pixelmatrix

object PixelMatrixConstants {

  val ESCAPE: Char = BigInt("033", 8).toChar

  val CODE_FG = 38

  val RESET: String = PixelMatrixConstants.ESCAPE + "[0m"

  val FLAG_BLINK_SLOW: String = PixelMatrixConstants.ESCAPE + "[5m"
  val FLAG_BLINK_RAPID: String = PixelMatrixConstants.ESCAPE + "[6m"
  val FLAG_BLINK_OFF: String = PixelMatrixConstants.ESCAPE + "[25m"
  val FLAG_INVERSE: String = PixelMatrixConstants.ESCAPE + "[7m"

}
