package com.example

object Utils {

  implicit class RichString(val s: String) extends AnyVal{
    def getOrElse(e: String): String =
      if (s.isEmpty) e else s

    def safeToInt(default: String = "0"): Int =
      s.filter(_.isDigit).getOrElse(default).toInt
  }
}
