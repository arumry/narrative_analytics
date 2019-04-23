package com.narrative.analytics

import com.mchange.v2.c3p0.ComboPooledDataSource
import org.squeryl.adapters.H2Adapter
import org.squeryl.Session
import org.squeryl.SessionFactory
import org.slf4j.{Logger, LoggerFactory}

trait DBConnectionPool {
  val logger: Logger = LoggerFactory.getLogger(getClass)

  val databaseUsername = "root"
  val databasePassword = "root"
  val databaseConnection = "jdbc:h2:mem:analytics"

  var cpds = new ComboPooledDataSource

  def configureDb() {
    cpds.setDriverClass("org.h2.Driver")
    cpds.setJdbcUrl(databaseConnection)
    cpds.setUser(databaseUsername)
    cpds.setPassword(databasePassword)

    cpds.setMinPoolSize(1)
    cpds.setAcquireIncrement(1)
    cpds.setMaxPoolSize(5)

    SessionFactory.concreteFactory = Some(() => connection)

    def connection = {
      logger.info("Creating connection with c3po connection pool")
      Session.create(cpds.getConnection, new H2Adapter)
    }
  }

  def closeDbConnection() {
    logger.info("Closing c3po connection pool")
    cpds.close()
  }
}
