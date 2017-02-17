name := "campaingservice"
version := "0.0.1-SNAPSHOT"
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

mainClass in assembly := Some("com.zcia.microservice.example.campaing.layer.Main")
assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false)

val meta = """META.INF(.)*""".r
assemblyMergeStrategy in assembly := {
  case PathList("javax", "servlet", xs @ _*) => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".html" => MergeStrategy.first
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case n if n.endsWith(".conf") => MergeStrategy.concat
  case meta(_) => MergeStrategy.discard
  case x => MergeStrategy.first
}
