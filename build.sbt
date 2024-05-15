import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.DefaultBuildSettings.{defaultSettings, scalaSettings}

val appName = "register-for-exchange-of-information"
val silencerVersion = "1.7.16"

ThisBuild/ majorVersion := 0
ThisBuild/ scalaVersion := "2.13.12"

lazy val scalaCompilerOptions = Seq(
  "-Xlint:-missing-interpolator,_",
  "-Ywarn-unused:imports",
  "-Ywarn-unused:privates",
  "-Ywarn-unused:locals",
  "-Ywarn-unused:explicits",
  "-Ywarn-unused:implicits",
  "-Ywarn-value-discard",
  "-Ywarn-unused:patvars",
  "-Ywarn-dead-code",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:implicitConversions"
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    Compile / scalafmtOnCompile := true,
    Test / scalafmtOnCompile := true,
    scalacOptions ++= scalaCompilerOptions,
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:cat=unused-imports&src=routes/.*:s",
    PlayKeys.playDefaultPort := 10016
  )
  .settings(ScoverageSettings.settings: _*)

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test")
  .settings(DefaultBuildSettings.itSettings)
  .settings(
    libraryDependencies ++= AppDependencies.test,
    resolvers += Resolver.jcenterRepo
  )
