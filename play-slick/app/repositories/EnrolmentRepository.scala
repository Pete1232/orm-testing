package repositories

import java.sql.Timestamp
import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.{Enrolment, Service}
import play.api.db.slick.DatabaseConfigProvider
import play.db.NamedDatabase
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EnrolmentRepository @Inject()(@NamedDatabase("enrolmentstore") val dbConfigProvider: DatabaseConfigProvider) {

  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class ServiceTable(tag: Tag) extends Table[Service](tag, "service") {

    def serviceId = column[UUID]("serviceId", O.PrimaryKey)

    def serviceName = column[String]("serviceName")

    override def * = (serviceId, serviceName) <>
      ((Service.apply _).tupled, Service.unapply)
  }

  private val services = TableQuery[ServiceTable]

  private class EnrolmentTable(tag: Tag) extends Table[Enrolment](tag, "enrolments") {

    def enrolmentId = column[UUID]("enrolmentId", O.PrimaryKey)

    def enrolmentKey = column[String]("enrolmentKey")

    def enrolToken = column[String]("enrolToken")

    def dateEnrolled = column[Timestamp]("dateEnrolled")

    def datePrint = column[Option[Timestamp]]("datePrint")

    def dateActivated = column[Option[Timestamp]]("dateActivated")

    def failedActivationCount = column[Int]("failedActivationCount", O.Default(0))

    def friendlyName = column[Option[String]]("friendlyName")

    def serviceId = column[UUID]("serviceId")

    def sid = foreignKey("service_fk", serviceId, services)(_.serviceId, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

    override def * = (enrolmentId, enrolmentKey, enrolToken, dateEnrolled, datePrint, dateActivated, failedActivationCount, friendlyName, serviceId) <>
      ((Enrolment.apply _).tupled, Enrolment.unapply)
  }

  private val enrolments = TableQuery[EnrolmentTable]

  val createStmts = enrolments.schema.create.statements ++ services.schema.create.statements

  val dropStmts = enrolments.schema.drop.statements ++ services.schema.drop.statements

  def create(enrolmentId: UUID, enrolmentKey: String, enrolToken: String, dateEnrolled: Timestamp, failedActivationCount: Int, serviceId: UUID)
            (implicit ec: ExecutionContext): Future[Unit] =
    db.run {
      DBIO.seq(
        services
          .map(s => (s.serviceId, s.serviceName))
          .+=((serviceId, "IR-SA")),
        enrolments
          .map(e => (e.enrolmentId, e.enrolmentKey, e.enrolToken, e.dateEnrolled, e.failedActivationCount, e.serviceId))
          .+=((enrolmentId, enrolmentKey, enrolToken, dateEnrolled, failedActivationCount, serviceId))
      )
    }

  def readOne(enrolmentKey: String)(implicit ec: ExecutionContext): Future[Enrolment] =
    db.run {
      enrolments.filter(_.enrolmentKey === enrolmentKey).result
    }.map(_.head)

  def update(oldKey: String, newKey: String): Future[Int] = {
    val q = for {c <- enrolments if c.enrolmentKey === oldKey} yield c.enrolmentKey
    db.run {
      q.update(newKey)
    }
  }

  def getService(enrolmentKey: String)(implicit ec: ExecutionContext): Future[String] = {
    val enr = enrolments.filter(_.enrolmentKey === enrolmentKey)

    db.run {
      (for {
        (e, s) <- enr join services on (_.serviceId === _.serviceId)
      } yield s.serviceName).result
    }.map(_.head)
  }

  def list(): Future[Seq[Enrolment]] = db.run {
    enrolments.result
  }
}