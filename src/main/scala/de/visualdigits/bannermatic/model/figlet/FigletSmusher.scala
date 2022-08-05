package de.visualdigits.bannermatic.model.figlet

import de.visualdigits.bannermatic.model.figlet.`type`.Direction

import scala.collection.mutable

case class FigletSmusher(
                     direction: Direction = Direction.auto,
                     font: FigletFont
                   ) {

  private val SM_EQUAL = 1       // smush equal chars (not hardblanks)
  private val SM_LOWLINE = 2     // smush _ with any char in hierarchy
  private val SM_HIERARCHY = 4   // hierarchy: |, /\, [], {}, (), <>
  private val SM_PAIR = 8        // hierarchy: [ + ] -> |, { + } -> |, ( + ) -> |
  private val SM_BIGX = 16       // / + \ -> X, > + < -> X
  private val SM_HARDBLANK = 32  // hardblank + hardblank -> hardblank
  private val SM_KERN = 64
  private val SM_SMUSH = 128
  private val PAIRS = List("[]", "{}", "()")

  def smushRow(al: String, curChar: List[String], row: Int, maxSmush: Int, curCharWidth: Int, prevCharWidth: Int): (String, String) = {
    var addLeft = al
    var addRight: String = curChar(row)

    direction match {
      case Direction.right_to_left => {
        val temp = addRight
        addRight = addLeft
        addLeft = temp
      }
      case _ =>
    }

    for (i <- 0 until maxSmush) {
      val(left, idx) = getLeftSmushedChar(i, addLeft, maxSmush)
      val right = addRight(i).toString
      val smushed = smushChars(left, right, curCharWidth, prevCharWidth)
      addLeft = updateSmushedCharInLeftBuffer(addLeft, idx, smushed)
    }

    (addLeft, addRight)
  }

  private def getLeftSmushedChar(i: Int, addLeft: String, maxSmush: Int): (String, Int) = {
    val idx = addLeft.length - maxSmush + i
    (if (idx >= 0 && idx < addLeft.length) addLeft(idx).toString else "", idx)
  }

  private def updateSmushedCharInLeftBuffer(addLeft: String, idx: Int, smushed: String): String = {
    if (idx < 0 || idx > addLeft.length) addLeft
    else {
      val l: Array[String] = addLeft.toCharArray.map(_.toString)
      l(idx) = smushed
      l.mkString("")
    }
  }

  def currentSmushAmount(buffer: Array[String], curChar: List[String], curCharWidth: Int, prevCharWidth: Int): Int = {
    if ((font.smushMode & (SM_SMUSH | SM_KERN)) == 0) return 0

    var maxSmush  = curCharWidth
    for (row <- 0 until font.height) {
      var lineLeft = buffer(row)
      var lineRight = curChar(row)
      if ("right-to-left" == direction) {
        val temp = lineLeft
        lineLeft = lineRight
        lineRight = temp
      }
      var linebd = rtrim(lineLeft).length - 1
      if (linebd < 0) linebd = 0
      var ch1: String = ""
      if (linebd < lineLeft.length) ch1 = lineLeft(linebd).toString

      val charbd = lineRight.length - ltrim(lineRight).length
      var ch2: String = ""
      if (charbd < lineRight.length) ch2 = lineRight(charbd).toString

      var amt = charbd + lineLeft.length - 1 - linebd

      if (ch1.isEmpty || ch1 == ' ') amt += 1
      else if (ch2.nonEmpty && smushChars(ch1, ch2, curCharWidth, prevCharWidth).nonEmpty) amt += 1

      if (amt < maxSmush) maxSmush = amt
    }
    maxSmush
  }

  private def smushChars(left: String = "", right: String = "", curCharWidth: Int, prevCharWidth: Int): String = {
    if (left.nonEmpty && left.trim.isEmpty) return right
    else if (right.nonEmpty && right.trim.isEmpty) return left

    if (prevCharWidth < 2 || curCharWidth < 2) return ""

    if ((font.smushMode & SM_SMUSH) == 0) return ""

    if ((font.smushMode & 63) == 0) {
      if (left == font.hardBlank) return right
      if (right == font.hardBlank) return left
      if ("right-to-left" == direction) return left else return right
    }

    if ((font.smushMode & SM_HARDBLANK) != 0 && left == font.hardBlank && right == font.hardBlank) return left

    if (left == font.hardBlank || right == font.hardBlank) return ""

    if ((font.smushMode & SM_EQUAL) != 0 && left == right) return left

    val smushes: mutable.Map[String, String] = mutable.Map()
    if ((font.smushMode & SM_LOWLINE) != 0) smushes.addOne("_", "|/\\\\[]{}()<>")
    if ((font.smushMode & SM_HIERARCHY) != 0) smushes.addAll(Map(
      "|" -> "|/\\\\[]{}()<>",
      "\\\\/" -> "[]{}()<>",
      "[]" -> "{}()<>",
      "{}" -> "()<>",
      "()" -> "<>"
    ))

    smushes.foreach(entry => {
      if (entry._1.contains(left) && entry._2.contains(right)) return right
      if (entry._2.contains(left) && entry._1.contains(right)) return left
    })

    if ((font.smushMode & SM_PAIR) != 0) List(left + right, right + left)
      .foreach(pair => if (PAIRS.contains(pair)) return "|")

    if ((font.smushMode & SM_BIGX) != 0) {
      if ("/" == left && "\\" == right) return "|"
      if ("/" == right && "\\" == left) return "Y"
      if (">" == left && "<" ==right) return "X"
    }

    ""
  }

  private def ltrim(s: String): String = s.replaceAll("^\\s+", "")

  private def rtrim(s: String): String = s.replaceAll("\\s+$", "")
}
