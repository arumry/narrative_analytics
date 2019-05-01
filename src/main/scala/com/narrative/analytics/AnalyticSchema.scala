package com.narrative.analytics

import com.narrative.analytics.dto.Analytic
import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, Table}



object AnalyticsSchema extends Schema {
  val analytics: Table[Analytic] = table[Analytic]("analytics")
  on(analytics)(s => declare(
    columns(s.timestamp) are indexed
  ))
}
