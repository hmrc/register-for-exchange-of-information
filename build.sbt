
val appName = "register-for-exchange-of-information"
val silencerVersion = "1.7.16"

ThisBuild/ majorVersion := 0
ThisBuild/ scalaVersion := "3.3.4"

lazy val scalaCompilerOptions = Seq(
  "-language:implicitConversions",

  // Silence repeated-flag noise
  "-Wconf:msg=Flag.*repeatedly:s",

  // Enable unused warnings
  "-Wunused:imports",
  "-Wunused:locals",
  "-Wunused:privates",
  "-Wunused:params",

  // Generated Twirl templates
  "-Wconf:msg=unused import&src=html/.*:s",

  // Play routes files (.routes are not under routes/)
  "-Wconf:msg=unused import&src=.*\\.routes:s",
  "-Wconf:msg=unused import&src=html/.*:s",

  "-Wvalue-discard",
  "-feature"
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
