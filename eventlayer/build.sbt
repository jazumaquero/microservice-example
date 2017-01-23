name := "eventlayer"
version := "1.0"
scalaVersion := "2.11.8"
scalacOptions := Seq("-unchecked", "-feature", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.4.11"
  val sprayV = "1.3.3"
  val scalaTestV = "3.0.0"

  Seq(
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-http-core"    % akkaV,
    "com.typesafe.akka"   %%  "akka-http-experimental"    % akkaV,
    "com.typesafe.akka"	  %%  "akka-http-spray-json-experimental"	% akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "com.typesafe.akka"   %%  "akka-http-testkit" % akkaV,
    "org.scalatest"       %%  "scalatest"     % scalaTestV % "test",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
    "ch.qos.logback" % "logback-classic" % "1.1.3",
    "org.slf4j" % "slf4j-nop" % "1.6.4"
  )
}