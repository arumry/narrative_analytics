package com.narrative.analytics

import java.sql.Timestamp

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, Table}


object Event extends Enumeration {
  type Interface = Value
  val Click: Event.Value = Value(1, "click")
  val Impression: Event.Value = Value(2, "impression")

  def isOfType(s: String): Boolean = values.exists(_.toString == s)
}



case class AnalyticAggregation(uniqueUsers: Set[String], numClicks: Int, numImpressions: Int) {
  override def toString: String = {
    val numUniqueUsers = uniqueUsers.size
    s"unique_users,$numUniqueUsers\nclicks,$numClicks\nimpressions,$numImpressions"
  }
}


case class Analytic(id: Long,
                    timestamp: Timestamp,
                    user: String,
                    event: Event.Interface) {
  def this() = this(0, new Timestamp(System.currentTimeMillis()), "", Event.Click)
}



object AnalyticModel {
  def create(analytic: Analytic): Unit = {
    inTransaction {
      AnalyticsSchema.analytics.insert(analytic)
    }
  }

  def getBetween(start: Timestamp, end: Timestamp): AnalyticAggregation = {
    val rows = from(AnalyticsSchema.analytics)(s =>
      where(s.timestamp between(start, end))
        select(s.user, s.event)
    )
    rows.toVector.foldLeft(AnalyticAggregation(Set(), 0, 0))((a, r) => {
      val isClick = r._2 == Event.Click
      val numClicks = if (isClick) a.numClicks + 1 else a.numClicks
      val numImpressions = if (!isClick) a.numImpressions + 1 else a.numImpressions
      a.copy(uniqueUsers = a.uniqueUsers + r._1, numClicks = numClicks, numImpressions = numImpressions)
    })
  }
}


object AnalyticsSchema extends Schema {
  val analytics: Table[Analytic] = table[Analytic]("analytics")
  on(analytics)(s => declare(
    columns(s.timestamp) are indexed
  ))
}
