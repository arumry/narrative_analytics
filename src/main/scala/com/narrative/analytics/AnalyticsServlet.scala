package com.narrative.analytics

import java.sql.Timestamp

import com.narrative.analytics.dao.{AnalyticDAO, AnalyticSchema}
import com.narrative.analytics.dto.{AnalyticDTO, Event}

import scala.util.Try
import org.scalatra._
import org.slf4j.{Logger, LoggerFactory}

class AnalyticsServlet extends ScalatraServlet with DBSessionSupport {
  val MILLI_IN_HALF_HOUR = 1800000L
  val logger: Logger = LoggerFactory.getLogger(getClass)

  private def parseLong(s: String): Option[Long] = {
    val parsedValue = Try { Some(s.toLong) }
    parsedValue.getOrElse(None)
  }

  private def getMilli(t: Option[String]): Option[Long] = {
    t.flatMap(parseLong)
      .filter(x => x > MILLI_IN_HALF_HOUR && x < System.currentTimeMillis())
  }


  get("/analytics") {

    val milli = getMilli(params.get("timestamp"))
    milli.getOrElse(halt(400, "You must provide a valid timestamp"))

    val analyticAggResult = milli
      .map(x => (new Timestamp(x - MILLI_IN_HALF_HOUR), new Timestamp(x + MILLI_IN_HALF_HOUR)))
      .map(x => AnalyticDAO.getBetween(x._1, x._2))
      .get

    analyticAggResult.getOrElse({
      logger.error(analyticAggResult.toString)
      InternalServerError()
    })
  }

  post("/analytics") {

    val timestamp = getMilli(params.get("timestamp")).map(new Timestamp(_))
    val user = params.get("user").filter(!_.isEmpty)
    val event = params.get("event").filter(Event.isOfType).map(x => Event.withName(x))

    timestamp.getOrElse(halt(400, "You must provide a valid timestamp"))
    user.getOrElse(halt(400, "You must provide a valid user"))
    event.getOrElse(halt(400, "You must provide a valid event: click | impression"))

    val analytic = AnalyticDTO(0, timestamp.get, user.get, event.get)
    val result = AnalyticDAO.create(analytic)

    result.map(_ => NoContent()).getOrElse({
      logger.error(result.toString)
      InternalServerError()
    })
  }

  // Here for convenience during development; would evolve table DDL separately in the future
  post("/analytics-create-db") {
    AnalyticSchema.create
    Ok()
  }

  notFound {
    MethodNotAllowed()
  }

}
