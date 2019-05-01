package com.narrative.analytics.dto

import java.sql.Timestamp

case class AnalyticAggregation(uniqueUsers: Set[String], numClicks: Int, numImpressions: Int) {
  override def toString: String = {
    val numUniqueUsers = uniqueUsers.size
    s"unique_users,$numUniqueUsers\nclicks,$numClicks\nimpressions,$numImpressions"
  }
}
