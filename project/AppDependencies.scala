import sbt._

object AppDependencies {

  private val bootstrapVersion = "10.5.0"
  private val mongoVersion     = "2.11.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"        % mongoVersion,
    "uk.gov.hmrc"       %% "domain-play-30"            % "11.0.0"
  )

  def test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion % Test,
    "org.playframework"       %% "play-test"                  % "3.0.10"         % Test,
    "org.scalatestplus"       %% "scalacheck-1-19"            % "3.2.19.0"       % Test,
    "io.github.wolfendale"    %% "scalacheck-gen-regexp"      % "1.1.0"          % Test,
    "org.jsoup"                % "jsoup"                      % "1.22.1"         % Test,
    "org.scalatestplus.play"  %% "scalatestplus-play"         % "7.0.2"          % Test
  )

}
