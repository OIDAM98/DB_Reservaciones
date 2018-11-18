
import Dependencies._
import sbt.Keys.version

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.dulioscar",
      scalaVersion := "2.12.5",
      name := "FinalProjDB",
      version := "0.1"
    )),
    scalacOptions += "-Ypartial-unification", // 2.11.9+

    libraryDependencies ++= Seq(
      // Start with this one
      "org.tpolecat" %% "doobie-core"      % "0.6.0",

      // And add any of these as needed
      "org.tpolecat" %% "doobie-postgres"  % "0.6.0",          // Postgres driver 42.2.5 + type mappings.
      "org.tpolecat" %% "doobie-specs2"    % "0.6.0" % "test", // Specs2 support for typechecking statements.
      "org.tpolecat" %% "doobie-scalatest" % "0.6.0" % "test",  // ScalaTest support for typechecking statements.
      "org.scalacheck" %% "scalacheck" % "1.14.0" % "test"
    ),
    libraryDependencies += "org.scala-lang.modules" % "scala-swing_2.12" % "2.0.3",
    name := "Main",
    mainClass in (Compile, run) := Some("Main"),
    libraryDependencies += scalaTest % Test
  )