name := "little-io"
version := "1.1.0-SNAPSHOT"
organization := "losizm"

scalaVersion := "2.12.6"
scalacOptions ++= Seq("-deprecation", "-feature", "-Xcheckinit")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
