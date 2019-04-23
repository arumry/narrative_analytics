package com.narrative.analytics

import org.squeryl.{Session, SessionFactory}
import org.scalatra._

object DBSessionSupport {
  val key: String = {
    val n = getClass.getName
    if (n.endsWith("$")) n.dropRight(1) else n
  }
}

trait DBSessionSupport { this: ScalatraBase =>
  import DBSessionSupport._

  def dbSession:Session = request.get(key).orNull.asInstanceOf[Session]

  before() {
    request(key) = SessionFactory.newSession
    dbSession.bindToCurrentThread
  }

  after() {
    dbSession.close
    dbSession.unbindFromCurrentThread
  }

}
