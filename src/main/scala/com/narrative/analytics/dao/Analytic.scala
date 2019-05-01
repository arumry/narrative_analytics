package com.narrative.analytics.dao

import java.sql.Timestamp

import com.narrative.analytics.AnalyticsSchema
import com.narrative.analytics.dto.{Analytic, AnalyticAggregation, Event}
import org.squeryl.PrimitiveTypeMode._

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
