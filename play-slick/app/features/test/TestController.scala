package features.test

import java.sql.{Date, Timestamp}
import java.util.UUID
import javax.inject.Inject

import play.api.mvc.{Action, Controller}
import repositories.EnrolmentRepository

import scala.concurrent.ExecutionContext

class TestController @Inject()(enrolmentRepository: EnrolmentRepository)
                              (implicit ec: ExecutionContext) extends Controller {

  val getSchema = Action { request =>
    val stmts = enrolmentRepository.createStmts ++ enrolmentRepository.dropStmts
    Ok(stmts.toSeq.map(_.toString).toString)
  }

  val runTest = Action.async { request =>

    val ts = Timestamp.from(new java.util.Date().toInstant)

    enrolmentRepository.create(UUID.randomUUID(), "IR-SA", "ABC123XYZ", ts, 0, UUID.randomUUID())
      .flatMap(_ =>
        enrolmentRepository.list().map(
          enr => Ok(enr.toString())
        )
      )
  }
}
