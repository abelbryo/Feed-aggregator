name := """feed-aggragator"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.akka23-SNAPSHOT",
  "joda-time"         %  "joda-time"           % "2.0",
  "org.webjars"       %% "webjars-play"        % "2.3.0",
  "org.webjars"       %  "bootstrap"           % "3.1.1-2",
  "jp.t2v"            %% "play2-auth"          % "0.13.0",
  "jp.t2v"            %% "play2-auth-test"     % "0.13.0"  % "test",
  "org.mindrot"       %  "jbcrypt"             % "0.3m"
)

scalacOptions ++= Seq(
  "-encoding",
  "UTF-8",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions"
)

