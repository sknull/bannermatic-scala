package de.visualdigits.bannermatic.model.figlet

import de.visualdigits.bannermatic.model.figlet.`type`.{Direction, Justify}

import scala.collection.mutable

class Figlet(fontName: String = "default", width: Int = 80, direction: Direction = Direction.auto, justify: Justify = Justify.auto) {

  private var text: IndexedSeq[Int] = _
  private var font: FigletFont = _

  private var iterator = 0
  private var maxSmush = 0

  private var curCharWidth = 0
  private var prevCharWidth = 0
  private var currentTotalWidth = 0

  private var blankMarkers: mutable.Stack[(Array[String], Int)] = _
  private val queue: mutable.Stack[Array[String]] = mutable.Stack()
  private var buffer: Array[String] = _

  private var smusher: FigletSmusher = _

  this.font = FigletFont("fonts/"+ fontName + ".flf")
  this.blankMarkers = mutable.Stack()
  this.buffer = Array.fill[String](this.font.height)("")
  this.smusher = FigletSmusher(direction, this.font)

  def renderText(text: String): String = {
    this.text = text.map(_.toInt)
    while (iterator < text.length) {
      addCharToProduct()
      iterator += 1
    }
    if (buffer(0).nonEmpty) queue.addOne(buffer)
    var stringAcc = ""
    queue.foreach(buffer => {
      val b: Array[String] = justifyString(justify, buffer)
      stringAcc += b.mkString("\n").replace(font.hardBlank, " ") + "\n"
    })
    stringAcc
  }

  def addCharToProduct(): Unit = {
    val tuple = (buffer.clone(), iterator)
    val c = text(iterator)
    if (c == '\n') {
      blankMarkers.addOne(tuple)
      handleNewline()
    } else {
      val curChar = font.chars.get(text(iterator))
      if (curChar.nonEmpty) {
        curCharWidth = font.width.getOrElse(text(iterator), 0)
        if (width < curCharWidth) throw new IllegalStateException("No space left to print char")
        maxSmush = if (curChar.nonEmpty) smusher.currentSmushAmount(buffer, curChar.get, curCharWidth, prevCharWidth) else 0
        currentTotalWidth = buffer(0).length + curCharWidth- maxSmush
        if (c == ' ') blankMarkers.addOne(tuple)
        if (c == '\n') {
          blankMarkers.addOne(tuple)
          handleNewline()
        }
        if (currentTotalWidth >= width) handleNewline()
        else for (row <- 0 until font.height) {
          val(addLeft, addRight) = smusher.smushRow(buffer(row), curChar.get, row, maxSmush, curCharWidth, prevCharWidth)
          buffer(row) = addLeft + addRight.substring(maxSmush)
        }
        prevCharWidth = curCharWidth
      }
    }
  }

  def handleNewline(): Unit = {
    if (blankMarkers.nonEmpty) {
      val(savedBuffer, savedIterator) = blankMarkers.pop
      queue.addOne(savedBuffer)
      iterator = savedIterator
    }
    else {
      queue.addOne(buffer)
      iterator -= 1
    }

    currentTotalWidth = buffer(0).length
    buffer = Array.fill[String](font.height)("")
    blankMarkers = mutable.Stack[(Array[String], Int)]()
    prevCharWidth = 0
    val curChar: Option[List[String]] = font.chars.get(text(iterator))
    if (curChar.nonEmpty) maxSmush = smusher.currentSmushAmount(buffer, curChar.get, curCharWidth, prevCharWidth)
  }

  def justifyString(justify: Justify, buffer: Array[String]): Array[String] = {
    justify match {
      case Justify.right => buffer.map(row => " " * (width - row.length) + row)
      case Justify.center => buffer.map(row => " " * ((width - row.length) / 2) + row)
      case _ => buffer
    }
  }
}

object Figlet {
  def apply(font: String = "default", width: Int = 80, direction: Direction = Direction.auto, justify: Justify = Justify.auto) = new Figlet(font, width, direction, justify)
}