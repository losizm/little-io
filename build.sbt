organization := "com.github.losizm"
name         := "little-io"
version      := "4.1.0"
description  := "The Scala library that provides extension methods to java.io and java.nio"
homepage     := Some(url("https://github.com/losizm/little-io"))
licenses     := List("Apache License, Version 2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

scalaVersion       := "2.13.4"
crossScalaVersions := Seq("2.12.12")

scalacOptions ++= Seq("-deprecation", "-feature", "-Xcheckinit")

Compile / doc / scalacOptions ++= Seq(
  "-doc-title",   name.value,
  "-doc-version", version.value
)

Compile / unmanagedSourceDirectories += {
  sourceDirectory.value / s"scala-${scalaBinaryVersion.value}"
}

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.2" % "test"

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
