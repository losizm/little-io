organization := "com.github.losizm"
name         := "little-io"
version      := "6.0.0"
description  := "The Scala library that provides extension methods to java.io and java.nio"
homepage     := Some(url("https://github.com/losizm/little-io"))
licenses     := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion := "3.0.2"

scalacOptions ++= Seq("-deprecation", "-feature", "-new-syntax", "-Xfatal-warnings", "-Yno-experimental")

Compile / doc / scalacOptions ++= Seq(
  "-project", name.value,
  "-project-version", {
    val ver = version.value
    ver.substring(0, ver.lastIndexOf(".")) ++ ".x"
  }
)

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.9" % "test"

developers := List(
  Developer(
    id    = "losizm",
    name  = "Carlos Conyers",
    email = "carlos.conyers@hotmail.com",
    url   = url("https://github.com/losizm")
  )
)

scmInfo := Some(
  ScmInfo(
    url("https://github.com/losizm/little-io"),
    "scm:git@github.com:losizm/little-io.git"
  )
)

publishMavenStyle := true

pomIncludeRepository := { _ => false }

publishTo := {
  val nexus = "https://oss.sonatype.org"
  isSnapshot.value match {
    case true  => Some("snaphsots" at s"$nexus/content/repositories/snapshots")
    case false => Some("releases"  at s"$nexus/service/local/staging/deploy/maven2")
  }
}
