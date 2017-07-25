# --- !Ups

create table `enrolments` (`enrolmentId` BINARY(16) NOT NULL PRIMARY KEY,`enrolmentKey` TEXT NOT NULL,`enrolToken` TEXT NOT NULL,`dateEnrolled` DATETIME,`datePrint` DATETIME,`dateActivated` DATETIME,`failedActivationCount` INTEGER DEFAULT 0 NOT NULL,`friendlyName` TEXT)

# --- !Downs

drop table `enrolments`
