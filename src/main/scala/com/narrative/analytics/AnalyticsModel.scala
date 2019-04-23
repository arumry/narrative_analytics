package com.narrative.analytics

import java.sql.Timestamp

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, Table}


object Event extends Enumeration {
  type Interface = Value
  val Click: Event.Value = Value(1, "Click")
  val Impression: Event.Value = Value(2, "Impression")

  def isOfType(s: String): Boolean = values.exists(_.toString == s)
}


class Analytic(val id: Long,
               val timestamp: Timestamp,
               val user: String,
               val event: Event.Interface) {
  def this() = this(0, new Timestamp(System.currentTimeMillis()), "", Event.Click)
}

case class AnalyticAgg(uniqueUsers: Set[String], numClicks: Int, numImpressions: Int) {
  override def toString: String = {
    val numUniqueUsers = uniqueUsers.size
    s"unique_users,$numUniqueUsers\nclicks,$numClicks\nimpressions,$numImpressions"
  }
}

object Analytic {
  def create(analytic: Analytic): Boolean = {
    inTransaction {
      Analytics.analytics.insert(analytic)
      true
    }
  }

  def getForTheHour(start: Timestamp, end: Timestamp): AnalyticAgg = {
    val rows = from(Analytics.analytics)(s =>
      where(s.timestamp between(start, end))
        select(s.user, s.event)
    )
    rows.toVector.foldLeft(AnalyticAgg(Set(), 0, 0))((a, r) => {
      val isClick = r._2 == Event.Click
      val numClicks = if (isClick) a.numClicks + 1 else a.numClicks
      val numImpressions = if (!isClick) a.numImpressions + 1 else a.numImpressions
      a.copy(uniqueUsers = a.uniqueUsers + r._1, numClicks = numClicks, numImpressions = numImpressions)
    })
  }
}


object Analytics extends Schema {
  val analytics: Table[Analytic] = table[Analytic]("analytics")
  on(analytics)(s => declare(
    columns(s.timestamp) are indexed
  ))
}
