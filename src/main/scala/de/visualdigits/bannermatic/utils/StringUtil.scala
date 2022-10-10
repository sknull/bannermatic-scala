package de.visualdigits.bannermatic.utils

import java.io.{BufferedInputStream, InputStream}
import java.nio.charset.{Charset, StandardCharsets}
import java.util

object StringUtil {

  /**
   * Wraps the given string value into an option and also treats empty strings as None.
   *
   * @param value The string value.
   * @return Option[String]
   */
  def stringOption(value: String): Option[String] = {
    val option = Option(value)
    if (option.getOrElse("").nonEmpty) {
      option
    } else {
      None
    }
  }

  /**
   * Wraps the given long value into an option and also treats 0 values as None.
   *
   * @param value The long value.
   * @return Option[Long]
   */
  def longOption(value: Long): Option[Long] = {
    val option = Option(value)
    if (option.getOrElse(0) != 0) {
      option
    } else {
      None
    }
  }

  /**
   * Wraps the given long value into an option and also treats 0 values as None.
   *
   * @param value The int value.
   * @return Option[Long]
   */
  def intOption(value: Int): Option[Int] = {
    val option = Option(value)
    if (option.getOrElse(0) != 0) {
      option
    } else {
      None
    }
  }

  /**
   * Loads the given resource as an UTF-8 string.
   *
   * @param resource The resource to obtain.
   * @param encoding The encoding to use (defaults to UTF-8).
   * @return String
   */
  def loadResourceAsString(resource: String, encoding: Charset = StandardCharsets.UTF_8): String = new String(loadFromFile(resource), encoding)

  /**
   * Loads the given resource as a byte array.
   *
   * @param resource The resource to obtain.
   * @return Array[Byte]
   */
  def loadFromFile(resource: String): Array[Byte] = {
    val ins = StringUtil.getClass.getClassLoader.getResourceAsStream(resource)
    if (ins != null) {
      getBytesFromInputSteam(ins)
    } else {
      Array()
    }
  }

  private def getBytesFromInputSteam(input: InputStream): Array[Byte] = {
    if (input == null) {
      return null
    }
    input match {
      case stream: BufferedInputStream =>
        return getBytesFromBufferedInputStream(stream)
      case _ =>
    }
    var current = 0
    val buffer = new util.ArrayList[Byte]()
    while ( {
      current = input.read
      current != -1
    }) {
      buffer.add(current.toByte)
    }
    val theBytes = new Array[Byte](buffer.size)
    current = 0
    while ( {
      current < buffer.size
    }) {
      theBytes(current) = buffer.get(current).byteValue
      current += 1
    }
    theBytes
  }

  private def getBytesFromBufferedInputStream(bInput: BufferedInputStream): Array[Byte] = {
    if (bInput == null) {
      return null
    }
    val theBytes = new Array[Byte](bInput.available)
    bInput.read(theBytes)
    theBytes
  }
}
