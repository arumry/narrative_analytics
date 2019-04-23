package com.narrative.analytics

import java.sql.Timestamp

import scala.util.Try
import org.scalatra._
import org.slf4j.{Logger, LoggerFactory}

class AnalyticsServlet extends ScalatraServlet with DBSessionSupport {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  private def isNumber(str: String): Boolean = str.forall(_.isDigit)
  private def getMilli(t: Option[String]):Option[Long] = {
    params.get("timestamp").filter(isNumber).map(x => x.toLong)
      .filter(x => x > 0 && x < System.currentTimeMillis())
  }

  get("/analytics") {
    val MILLI_IN_HALF_HOUR = 1800000L
    val analyticAggResult = Try {
      getMilli(params.get("timestamp"))
        .map(x => (new Timestamp(x - MILLI_IN_HALF_HOUR), new Timestamp(x + MILLI_IN_HALF_HOUR)))
        .map(x => Analytic.getForTheHour(x._1, x._2))
        .getOrElse("unique_users,0\nclicks,0\nimpressions,0")
    }

    analyticAggResult.getOrElse({
      logger.error(analyticAggResult.toString)
      InternalServerError()
    })
  }

  post("/analytics") {
    val result = Try {
      val analytic = for {
        timestamp <- getMilli(params.get("timestamp")).map(x => new Timestamp(x))
        user <- params.get("user").filter(!_.isEmpty)
        event <- params.get("event").filter(Event.isOfType).map(x => Event.withName(x))
      } yield new Analytic(0, timestamp, user, event)
      analytic.foreach(x => Analytic.create(x))
    }

    result.map(_ => NoContent()).getOrElse({
      logger.error(result.toString)
      InternalServerError()
    })
  }

  // Here for convenience during development; would evolve table DDL separately in the future
  post("/analytics-create-db") {
    Analytics.create
    Ok()
  }

  notFound {
    MethodNotAllowed()
  }

}
