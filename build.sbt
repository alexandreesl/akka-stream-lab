name := "Akka-stream-lab"
version := "1.0"
scalaVersion := "2.12.5"


enablePlugins(JavaAppPackaging)

lazy val Versions = new {
  val akkaVersion = "2.5.11"
}

lazy val akkaDependencies = Seq(
  "com.typesafe.akka" %% "akka-http" % "10.1.5",
  "com.typesafe.akka" %% "akka-actor" % Versions.akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % Versions.akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % Versions.akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % Versions.akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % Versions.akkaVersion % Test
)

lazy val loggingDependencies = Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.slf4j" % "slf4j-api" % "1.7.25"
)

libraryDependencies ++= (
  akkaDependencies++
  loggingDependencies
  )