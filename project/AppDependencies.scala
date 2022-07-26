import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-28" % "5.24.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.68.0",
    "uk.gov.hmrc" %% "domain" % "8.0.0-play-28"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-28" % "5.8.0" % Test,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-test-play-28" % "0.68.0" % Test,
    "org.mockito" %% "mockito-scala" % "1.16.42" % Test,
    "com.typesafe.play" %% "play-test" % PlayVersion.current % Test,
    "org.scalatestplus" %% "scalatestplus-scalacheck" % "3.1.0.0-RC2" % Test,
    "wolfendale" %% "scalacheck-gen-regexp" % "0.1.2" % Test,
    "org.jsoup" % "jsoup" % "1.12.1" % Test,
    "org.scalatest" %% "scalatest" % "3.1.0" % Test,
    "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
    "com.github.tomakehurst" % "wiremock-standalone" % "2.27.0" % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test, it"
  )

}
