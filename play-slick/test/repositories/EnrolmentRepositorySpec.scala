package repositories

import java.sql.Timestamp
import java.util.UUID

import models.Enrolment
import testutils.IntegrationSpec

class EnrolmentRepositorySpec extends IntegrationSpec {

  lazy val repo = app.injector.instanceOf[EnrolmentRepository]

  "create" must {
    "insert a new enrolment" in {

      val ts = Timestamp.from(new java.util.Date().toInstant)

      val sid = UUID.randomUUID()

      val expectedEnrolment = Enrolment(UUID.randomUUID(), "IR-SA~UTR~123", "ABC123XYZ", ts, failedActivationCount = 0, serviceId = sid)

      val result = await {
        import expectedEnrolment._

        repo.create(enrolmentId, enrolmentKey, enrolToken, dateEnrolled, failedActivationCount, sid)
          .flatMap(_ => repo.list())
      }

      result.size mustBe 1
      result.head mustBe expectedEnrolment
    }
  }
  "read" must {
    "find an enrolment based on the enrolment key" in {

      val sid = UUID.randomUUID()

      val ts = Timestamp.from(new java.util.Date().toInstant)

      val expectedEnrolment = Enrolment(UUID.randomUUID(), "IR-SA", "ABC123XYZ", ts, failedActivationCount = 0, serviceId = sid)

      val result = await {
        import expectedEnrolment._

        repo.create(enrolmentId, enrolmentKey, enrolToken, dateEnrolled, failedActivationCount, sid)
          .flatMap(_ => repo.readOne("IR-SA"))
      }

      result mustBe expectedEnrolment
    }
  }
  "update" must {
    "update an enrolment with a new enrolment key" in {

      val sid = UUID.randomUUID()

      val ts = Timestamp.from(new java.util.Date().toInstant)

      val originalEnrolment = Enrolment(UUID.randomUUID(), "IR-SA", "ABC123XYZ", ts, failedActivationCount = 0, serviceId = sid)

      val result = await {
        import originalEnrolment._

        repo.create(enrolmentId, enrolmentKey, enrolToken, dateEnrolled, failedActivationCount, sid)
          .flatMap(_ => repo.update("IR-SA", "IR-SA-AGENT"))
          .flatMap(_ => repo.readOne("IR-SA-AGENT"))
      }

      result mustBe originalEnrolment.copy(enrolmentKey = "IR-SA-AGENT")
    }
  }
  "getService" must {
    "return the service name for an enrolment" in {
      val eid = UUID.randomUUID()
      val sid = UUID.randomUUID()

      val ts = Timestamp.from(new java.util.Date().toInstant)

      val expectedEnrolment = Enrolment(eid, "IR-SA~UTR~123", "ABC123XYZ", ts, failedActivationCount = 0, serviceId = sid)

      val result = await {
        import expectedEnrolment._

        repo.create(enrolmentId, enrolmentKey, enrolToken, dateEnrolled, failedActivationCount, sid)
          .flatMap(_ => repo.getService("IR-SA~UTR~123"))
      }

      result mustBe "IR-SA"
    }
  }
}
