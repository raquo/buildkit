import VersionHelper.{versionFmt, fallbackVersion}

// Makes sure to increment the version for local development
ThisBuild / version := dynverGitDescribeOutput.value
  .mkVersion(out => versionFmt(out, dynverSonatypeSnapshots.value), fallbackVersion(dynverCurrentDate.value))

ThisBuild / dynver := {
  val d = new java.util.Date
  sbtdynver.DynVer
    .getGitDescribeOutput(d)
    .mkVersion(out => versionFmt(out, dynverSonatypeSnapshots.value), fallbackVersion(d))
}

(ThisBuild / scalaVersion) := Versions.Scala_2_12

(ThisBuild / crossScalaVersions) := Seq(
  Versions.Scala_3,
  Versions.Scala_2_13,
  Versions.Scala_2_12
)

lazy val commonSettings = Seq(
  name := "Buildkit",
  organization := "com.raquo",
  normalizedName := "buildkit",
  homepage := Some(url("https://github.com/raquo/buildkit")),
  licenses += ("MIT", url("https://github.com/raquo/buildkit/blob/master/README.md")),
  scmInfo := Some(
    ScmInfo(url("https://github.com/raquo/buildkit"), "scm:git@github.com/raquo/buildkit.git")
  ),
  developers := List(
    Developer(id = "raquo", name = "Nikita Gazarov", email = "nikita@raquo.com", url = url("https://github.com/raquo"))
  ),
  (Test / parallelExecution) := false,
  (Test / publishArtifact) := false,
  pomIncludeRepository := { _ => false },
  sonatypeCredentialHost := "s01.oss.sonatype.org",
  sonatypeRepository := "https://s01.oss.sonatype.org/service/local"
)

lazy val noPublish = Seq(
  publishLocal / skip := true,
  publish / skip := true
)

lazy val root = project.in(file("."))
  .settings(commonSettings)
  .settings(
    scalacOptions ++= Seq(
      "-feature",
      "-language:higherKinds"
    ),
    scalacOptions ~= (_.filterNot(Set(
      "-Wunused:params",
      "-Ywarn-unused:params",
      "-Wunused:explicits"
    ))),
    (Compile / doc / scalacOptions) ~= (_.filter(_.startsWith("-Xplugin"))), // https://github.com/DavidGregory084/sbt-tpolecat/issues/36
    (Compile / doc / scalacOptions) ++= Seq(
      "-no-link-warnings" // Suppress scaladoc "Could not find any member to link for" warnings
    ),
    (Test / scalacOptions) ~= { options: Seq[String] =>
      options.filterNot { o =>
        o.startsWith("-Ywarn-unused") || o.startsWith("-Wunused")
      }
    }
  )
