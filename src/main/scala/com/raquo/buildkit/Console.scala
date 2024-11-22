package com.raquo.buildkit

/** Logs stuff sbt-style, without access to sbt */
object Console {

  // ANSI escape codes for colors
  private val RESET = "\u001B[0m" // resets to the initial "no color"
  private val GREEN = "\u001B[32m"
  private val YELLOW = "\u001B[33m"
  private val RED = "\u001B[31m"

  def debug(text: String): Unit = {
    println(s"[debug] $text")
  }

  def info(text: String): Unit = {
    println(s"[info] $text")
  }

  def success(text: String): Unit = {
    println(s"$GREEN[info] $text$RESET")
  }

  def warning(text: String): Unit = {
    println(s"$YELLOW[warn] $text$RESET")
  }

  def error(text: String, andThrow: Boolean = false): Unit = {
    println(s"$RED[error] $text$RESET")
    if (andThrow) {
      throw new Exception(text)
    }
  }
}
