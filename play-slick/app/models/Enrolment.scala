package models

import java.sql.Timestamp
import java.util.UUID

case class Enrolment(enrolmentId: UUID,
                     enrolmentKey: String,
                     enrolToken: String,
                     dateEnrolled: Timestamp,
                     datePrint: Option[Timestamp] = None,
                     dateActivated: Option[Timestamp] = None,
                     failedActivationCount: Int,
                     friendlyName: Option[String] = None)
