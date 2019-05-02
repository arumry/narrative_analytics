package com.narrative.analytics.dao

import java.sql.Timestamp

import com.narrative.analytics.dto.{AnalyticAggregationDTO, AnalyticDTO, Event}
import org.squeryl.PrimitiveTypeMode._

import scala.util.Try

object AnalyticDAO {
  def create(analytic: AnalyticDTO): Try[Unit] = {
    Try {
      inTransaction {
        AnalyticSchema.analytics.insert(analytic)
      }
    }
  }

  def getBetween(start: Timestamp, end: Timestamp): Try[AnalyticAggregationDTO] = {
    Try {
      val rows = from(AnalyticSchema.analytics)(s =>
        where(s.timestamp between(start, end))
          select(s.user, s.event)
      )
      rows.toVector.foldLeft(AnalyticAggregationDTO(Set(), 0, 0))((a, r) => {
        val isClick = r._2 == Event.Click
        val numClicks = if (isClick) a.numClicks + 1 else a.numClicks
        val numImpressions = if (!isClick) a.numImpressions + 1 else a.numImpressions
        a.copy(uniqueUsers = a.uniqueUsers + r._1, numClicks = numClicks, numImpressions = numImpressions)
      })
    }
  }
}
