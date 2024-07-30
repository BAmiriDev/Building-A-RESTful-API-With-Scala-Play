ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "SoloProject",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.13.10"
  )
  .enablePlugins(PlayScala)

resolvers += "HMRC-open-artefacts-maven2" at "https://open.artefacts.tax.service.gov.uk/maven2"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.8.2",
  "com.typesafe.play" %% "play-ws"   % "2.8.2",
  "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28" % "0.63.0",
  guice,
  "org.scalatest"     %% "scalatest" % "3.2.15" % Test,
  "org.scalamock"     %% "scalamock" % "5.2.0"  % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0"  % Test

)
scalacOptions += "-Xprint:typer"




