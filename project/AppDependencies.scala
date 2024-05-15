import play.core.PlayVersion
import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.5.0"
  private val mongoVersion     = "1.9.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"        % mongoVersion,
    "uk.gov.hmrc"       %% "domain-play-30"                    % bootstrapVersion
  )

  def test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion % Test,
    "org.mockito"             %% "mockito-scala"              % "1.17.31" % Test,
    "org.playframework"       %% "play-test"                  % "3.0.3" % Test,
    "org.scalatestplus"       %% "scalatestplus-scalacheck"   % "3.1.0.0-RC2" % Test,
    "wolfendale"              %% "scalacheck-gen-regexp"      % "0.1.2" % Test,
    "org.jsoup"                % "jsoup"                      % "1.17.2" % Test,
    "org.scalatest"           %% "scalatest"                  % "3.2.18" % Test,
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "7.0.1" % Test,
    "com.vladsch.flexmark"     % "flexmark-all"               % "0.64.8" % Test
  )

}
