package com.narrative.analytics.dto

object Event extends Enumeration {
  type Interface = Value
  val Click: Event.Value = Value(1, "click")
  val Impression: Event.Value = Value(2, "impression")

  def isOfType(s: String): Boolean = values.exists(_.toString == s)
}
