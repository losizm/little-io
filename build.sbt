name := "little-io"
version := "3.4.0"
organization := "com.github.losizm"

description := "The Scala library that provides extension methods to java.io and java.nio"
licenses := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))
homepage := Some(url("https://github.com/losizm/little-io"))

scalaVersion := "2.13.1"
scalacOptions ++= Seq("-deprecation", "-feature", "-Xcheckinit")

crossScalaVersions := Seq("2.12.10")

Compile / unmanagedSourceDirectories += {
  sourceDirectory.value / s"scala-${scalaBinaryVersion.value}"
}

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"

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
  if (isSnapshot.value) Some("snaphsots" at s"$nexus/content/repositories/snapshots")
  else Some("releases" at s"$nexus/service/local/staging/deploy/maven2")
}

