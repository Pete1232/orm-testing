name := """play-slick-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.11"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "2.1.0"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "2.1.0"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.6"

libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0" % "test"
libraryDependencies += "com.h2database" % "h2" % "1.4.196" % "test"
libraryDependencies += filters
