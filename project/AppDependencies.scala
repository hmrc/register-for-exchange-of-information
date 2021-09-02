import sbt._
import play.core.PlayVersion.current


object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28"  % "5.8.0",
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-play-28"         % "0.52.0"
  )

  val test = Seq(
    "org.scalatest"               %% "scalatest"                % "3.1.0",
    "com.typesafe.play"           %% "play-test"                % current,
    "org.pegdown"                 %  "pegdown"                  % "1.6.0",
    "org.scalatestplus.play"      %% "scalatestplus-play"       % "5.1.0",
    "org.mockito"                 %% "mockito-scala"            % "1.10.6",
    "com.github.tomakehurst"      %  "wiremock-standalone"      % "2.25.0",
    "org.scalatestplus"           %% "scalatestplus-scalacheck" % "3.1.0.0-RC2",
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % "5.8.0"             % Test,
    "uk.gov.hmrc.mongo"       %% "hmrc-mongo-test-play-28"    % "0.52.0"            % Test,
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.36.8"            % "test, it"
  )
}
