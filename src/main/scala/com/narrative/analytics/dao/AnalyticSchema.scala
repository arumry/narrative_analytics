package com.narrative.analytics.dao

import com.narrative.analytics.dto.AnalyticDTO
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, Table}



object AnalyticSchema extends Schema {
  val analytics: Table[AnalyticDTO] = table[AnalyticDTO]("analytics")
  on(analytics)(s => declare(
    columns(s.timestamp) are indexed
  ))
}