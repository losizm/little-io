name := "little-io"
version := "2.4.0"
organization := "com.github.losizm"

scalaVersion := "2.12.7"
scalacOptions ++= Seq("-deprecation", "-feature", "-Xcheckinit")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/losizm/little-io"),
    "scm:git@github.com:losizm/little-io.git"
  )
)

developers := List(
  Developer(
    id    = "losizm",
    name  = "Carlos Conyers",
    email = "carlos.conyers@hotmail.com",
    url   = url("https://github.com/losizm")
  )
)

description := "The Scala library that provides extension methods to java.io and java.nio"
licenses := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/losizm/little-io"))

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org"
  if (isSnapshot.value) Some("snaphsots" at s"$nexus/content/repositories/snapshots")
  else Some("releases" at s"$nexus/service/local/staging/deploy/maven2")
}

publishMavenStyle := true
