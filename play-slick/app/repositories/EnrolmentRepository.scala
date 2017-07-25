package repositories

import java.sql.Timestamp
import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.Enrolment
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnrolmentRepository @Inject()(@NamedDatabase("enrolmentstore") val dbConfigProvider: DatabaseConfigProvider) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class EnrolmentTable(tag: Tag) extends Table[Enrolment](tag, "enrolments") {

    def enrolmentId = column[UUID]("enrolmentId", O.PrimaryKey)

    def enrolmentKey = column[String]("enrolmentKey")

    def enrolToken = column[String]("enrolToken")

    def dateEnrolled = column[Timestamp]("dateEnrolled")

    def datePrint = column[Option[Timestamp]]("datePrint")

    def dateActivated = column[Option[Timestamp]]("dateActivated")

    def failedActivationCount = column[Int]("failedActivationCount", O.Default(0))

    def friendlyName = column[Option[String]]("friendlyName")

    override def * = (enrolmentId, enrolmentKey, enrolToken, dateEnrolled, datePrint, dateActivated, failedActivationCount, friendlyName) <>
      ((Enrolment.apply _).tupled, Enrolment.unapply)
  }

  private val enrolments = TableQuery[EnrolmentTable]

  val createStmts = enrolments.schema.create.statements

  val dropStmts = enrolments.schema.drop.statements

  def create(enrolmentId: UUID, enrolmentKey: String, enrolToken: String, dateEnrolled: Timestamp, failedActivationCount: Int)
            (implicit ec: ExecutionContext): Future[Unit] =
    db.run {
      DBIO.seq(
        enrolments
          .map(e => (e.enrolmentId, e.enrolmentKey, e.enrolToken, e.dateEnrolled, e.failedActivationCount))
          .+=((enrolmentId, enrolmentKey, enrolToken, dateEnrolled, failedActivationCount))
      )
    }

  def readOne(enrolmentKey: String)(implicit ec: ExecutionContext): Future[Enrolment] =
    db.run {
      enrolments.filter(_.enrolmentKey === enrolmentKey).result
    }.map(_.head)

  def update(oldKey: String, newKey: String): Future[Int] = {
    val q = for { c <- enrolments if c.enrolmentKey === oldKey } yield c.enrolmentKey
    db.run {
      q.update(newKey)
    }
  }

  def list(): Future[Seq[Enrolment]] = db.run {
    enrolments.result
  }
}