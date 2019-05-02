package com.narrative.analytics.dto

case class AnalyticAggregationDTO(uniqueUsers: Set[String], numClicks: Int, numImpressions: Int) {
  override def toString: String = {
    val numUniqueUsers = uniqueUsers.size
    s"unique_users,$numUniqueUsers\nclicks,$numClicks\nimpressions,$numImpressions"
  }
}
