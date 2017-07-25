# --- !Ups

create table `service` (`serviceId` BINARY(16) NOT NULL PRIMARY KEY,`serviceName` TEXT NOT NULL);
create table `enrolments` (`enrolmentId` BINARY(16) NOT NULL PRIMARY KEY,`enrolmentKey` TEXT NOT NULL,`enrolToken` TEXT NOT NULL,`dateEnrolled` TIMESTAMP NOT NULL,`datePrint` TIMESTAMP NULL,`dateActivated` TIMESTAMP NULL,`failedActivationCount` INTEGER DEFAULT 0 NOT NULL,`friendlyName` TEXT,`serviceId` BINARY(16) NOT NULL);
alter table `enrolments` add constraint `service_fk` foreign key(`serviceId`) references `service`(`serviceId`) on update RESTRICT on delete CASCADE;

# --- !Downs

ALTER TABLE `enrolments` DROP FOREIGN KEY `service_fk`;
drop table `service`;
drop table `enrolments`;
