package de.visualdigits.bannermatic.model.figlet

import java.io.{File, FileInputStream, InputStream}

import scala.collection.mutable
import scala.io.Source
import scala.util.matching.Regex

class FigletFont(resourcePath: String) {

  private val MAGIC_NUMBER = new Regex("^[tf]lf2.")
  private val END_MARKER = new Regex("(.)\\s*$")
  private val HEX_MATCH = new Regex("(?i)^0x")

  var hardBlank: String = ""
  var height: Int = 0
  var baseLine: Int = 0
  private var maxLength: Int = 0
  private var oldLayout: Int = 0
  private var commentLines: Int = 0
  var printDirection: Int = 0
  var smushMode: Int = 0

  val chars: mutable.Map[Int, List[String]] = mutable.Map[Int, List[String]]()
  val width: mutable.Map[Int, Int] = mutable.Map[Int, Int]()

  init()

  def init(): Unit = {
    val ins: InputStream = ClassLoader.getSystemClassLoader.getResourceAsStream(resourcePath)
    var lines: mutable.Stack[String] = readLines(ins)
    val header: String = lines.head
    if (MAGIC_NUMBER.findAllIn(header).isEmpty) throw new IllegalArgumentException("Invalid font: " + resourcePath)
    val headerParts = MAGIC_NUMBER.replaceAllIn(header, "").split(" ")
    val n = headerParts.length
    if (n < 6) throw new IllegalArgumentException("Invalid header for font: " + resourcePath)
    this.hardBlank = headerParts(0)(0).toString
    this.height = headerParts(1).toInt
    this.baseLine = headerParts(2).toInt
    this.maxLength = headerParts(3).toInt
    this.oldLayout = headerParts(4).toInt
    this.commentLines = headerParts(5).toInt
    this.printDirection = if (n > 6) headerParts(6).toInt else 0

    var fullLayout = if (n > 7) headerParts(7).toInt else 0
    if (fullLayout == 0) {
      if (this.oldLayout == 0) fullLayout = 64
      else if (this.oldLayout < 0) fullLayout = 0
      else fullLayout = (this.oldLayout & 31) | 128
    }
    this.smushMode = fullLayout

    lines = lines.tail.drop(commentLines)

    // Read ASCII standard character set 32 - 127
    for (i <- 32 until 127) {
      readChar(i, lines)
    }

    // Read ASCII extended character set
    var i = 127
    while (lines.nonEmpty) {
      val line = lines.head.trim
      val ii = line.split(" ")(0)
      val idx: Option[Int] = if (isNumeric(ii)) Some(Integer.valueOf(ii).toInt)
      else if (HEX_MATCH.findFirstIn(ii).nonEmpty) Some(Integer.valueOf(ii.substring(2), 16).toInt)
      else Option.empty
      if (idx.nonEmpty) {
        lines.pop
        i = idx.get
      }
      readChar(i, lines)
      i += 1
    }
  }

  def isNumeric(str: String): Boolean = {
    !throwsNumberFormatException(str.toLong) || !throwsNumberFormatException(str.toDouble)
  }

  def throwsNumberFormatException(f: => Any): Boolean = {
    try { f; false } catch { case e: NumberFormatException => true }
  }

  def readChar(i: Int, lines: mutable.Stack[String]): Unit = {
    var end: Option[Regex] = Option.empty
    var width = 0
    val chrs = mutable.ListBuffer[String]()
    for (_ <- 0 until this.height) {
      var line = lines.pop
      if (end.isEmpty) {
        end = END_MARKER.findFirstIn(line).map(x => new Regex(Regex.quote(x) + "{1,2}$"))
      }
      end.foreach(x => line = x.replaceAllIn(line, ""))
      if (line.length > width) width = line.length
      chrs.addOne(line)
    }
    if (chrs.mkString("").nonEmpty) {
      this.width.addOne(i, width)
      this.chars.addOne(i, chrs.toList)
    }
  }

  def readLines(ins: InputStream): mutable.Stack[String] = {
    val lines = new mutable.Stack[String]()
    val bufferedSource = Source.fromInputStream(ins)
    try {
      lines.addAll(bufferedSource.getLines)
    } finally {
      bufferedSource.close
    }
    lines
  }
}

object FigletFont {
  def apply(resourcePath: String) = new FigletFont(resourcePath)
}