package testutils

import org.scalatest.{BeforeAndAfterEach, MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.db.DBApi
import play.api.db.evolutions.Evolutions

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Awaitable, ExecutionContext}

trait IntegrationSpec extends WordSpec with MustMatchers with GuiceOneAppPerSuite with BeforeAndAfterEach {

  lazy val databaseApi = app.injector.instanceOf[DBApi]

  implicit lazy val ec = app.injector.instanceOf[ExecutionContext]

  override def beforeEach(): Unit = {
    super.beforeEach()
    Evolutions.applyEvolutions(databaseApi.database("enrolmentstore"))
  }

  override def afterEach(): Unit = {
    Evolutions.cleanupEvolutions(databaseApi.database("enrolmentstore"))
    super.afterEach()
  }

  def await[T](awaitable: Awaitable[T], duration: Duration = Duration.Inf) = Await.result(awaitable, duration)
}
