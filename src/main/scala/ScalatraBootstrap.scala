import com.narrative.analytics._
import org.scalatra._
import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with DBConnectionPool {
  override def init(context: ServletContext) {
    configureDb()
    context.mount(new AnalyticsServlet, "/*")
  }

  override def destroy(context: ServletContext) {
    closeDbConnection()
  }
}
