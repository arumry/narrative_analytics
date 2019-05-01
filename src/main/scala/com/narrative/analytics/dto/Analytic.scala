package com.narrative.analytics.dto

import java.sql.Timestamp

case class Analytic(id: Long,
                    timestamp: Timestamp,
                    user: String,
                    event: Event.Interface) {
  def this() = this(0, new Timestamp(System.currentTimeMillis()), "", Event.Click)
}

