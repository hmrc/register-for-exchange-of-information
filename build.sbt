
val appName = "register-for-exchange-of-information"
val silencerVersion = "1.7.16"

ThisBuild/ majorVersion := 0
ThisBuild/ scalaVersion := "3.3.7"

lazy val scalaCompilerOptions = Seq(
  "-language:implicitConversions",
  "-Wconf:msg=Flag.*repeatedly:s",
  "-Wconf:cat=unused-imports&src=html/.*:s",
  "-Wconf:msg=unused import&src=html/.*:s",
  "-Wunused:imports",
  "-Wunused:locals",
  "-Wunused:privates",
  "-Wunused:params",
  "-Wvalue-discard",
  "-Xlint:-missing-interpolator",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Wconf:src=routes/.*:s",
  "-Wconf:cat=unused-imports&src=routes/.*:s",
)

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .disablePlugins(JUnitXmlReportPlugin) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .settings(
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    Compile / scalafmtOnCompile := true,
    Test / scalafmtOnCompile := true,
    scalacOptions ++= scalaCompilerOptions,
    PlayKeys.playDefaultPort := 10016
  )
  .settings(ScoverageSettings.settings: _*)
