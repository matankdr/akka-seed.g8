import Dependencies._
import com.typesafe.config.ConfigFactory
import com.typesafe.sbt.SbtNativePackager.autoImport.packageName
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerExposedPorts
import com.typesafe.sbt.site.SitePlugin.autoImport.{addMappingsToSiteDir, siteSubdirName}
import de.heikoseeberger.sbtheader.HeaderPlugin.autoImport.{HeaderLicense, headerLicense}
import org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile
import sbt.Keys._
import sbt._
import sbtassembly.AssemblyKeys.assembly
import sbtassembly.AssemblyPlugin.autoImport.assemblyJarName
import sbtunidoc.ScalaUnidocPlugin.autoImport.ScalaUnidoc
import scoverage.ScoverageKeys.{coverageFailOnMinimum, coverageHighlighting, coverageMinimum}
import spray.revolver.RevolverPlugin.autoImport.reStart

object Settings {

  lazy val baseSettings = Seq(
    scalaVersion := V.scala,

    scalacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-language:existentials",
      "-language:higherKinds",
      "-language:implicitConversions",
      "-language:postfixOps",
      "-Xfuture",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-infer-any",
      "-Ywarn-unused-import",
      "-Xfatal-warnings",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-Ywarn-unused",
      "-Xlint"
    ),

    // format code
    scalafmtOnCompile in Compile := true,

    // header
    organization := "$organization$",
    headerLicense := Some(HeaderLicense.MIT("$year$", "$author$")),

    // hot reload
    mainClass in reStart := None,

    // coverage
    coverageMinimum := 70,
    coverageFailOnMinimum := false,
    coverageHighlighting := true,

    // uber jar
    test in assembly := {}
  )

  lazy val commonSettings = Seq(
    name := "common",
    libraryDependencies ++= commonDependencies
  )

  lazy val appSettings = baseSettings ++ Seq(
    name := "app",
    libraryDependencies ++= appDependencies,
    mainClass in run := Some("$package$.Server"),

    mainClass in reStart := Some("$package$.Server"),

    mainClass in assembly := Some("$package$.Server"),
    assemblyJarName in assembly := s"app-${version.value}.jar",

    // docker
    packageName := "$name;format="normalize"$",
    dockerExposedPorts := {
      val resourceFile = (resourceDirectory in Compile).value / "application"
      val config = ConfigFactory.parseFileAnySyntax(resourceFile).resolve()
      Seq(config.getInt("app.docker.port"))
    }
  )

  lazy val cliSettings = baseSettings ++ Seq(
    name := "cli",
    libraryDependencies ++= cliDependencies,
    mainClass in run := Some("$package$.Main"),

    mainClass in assembly := Some("$package$.Main"),
    assemblyJarName in assembly := s"cli-${version.value}.jar"
  )

  lazy val rootSettings = Seq(
    // scaladoc
    siteSubdirName in ScalaUnidoc := "api",
    addMappingsToSiteDir(mappings in(ScalaUnidoc, packageDoc), siteSubdirName in ScalaUnidoc)
  )

}
