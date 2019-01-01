name := "Akka-stream-lab"
version := "1.0"
scalaVersion := "2.12.5"


enablePlugins(JavaAppPackaging)

mainClass in Compile := Some("Main")

lazy val Versions = new {
  val akkaVersion = "2.5.11"
}

lazy val akkaDependencies = Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-csv" % "0.8",
  "com.typesafe.akka" %% "akka-stream-kafka" % "0.22",
  "com.typesafe.akka" %% "akka-actor" % Versions.akkaVersion,
  "com.typesafe.akka" %% "akka-stream" % Versions.akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % Versions.akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % Versions.akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream-testkit" % Versions.akkaVersion % Test,
  "com.typesafe.akka" %% "akka-testkit" % Versions.akkaVersion % Test
)

lazy val testDependencies = Seq(
  "org.scalacheck" %% "scalacheck" % "1.13.4" % Test,
  "org.scalamock" %% "scalamock" % "4.1.0" % Test,
  "org.mockito" % "mockito-core" % "2.19.0" % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

lazy val loggingDependencies = Seq(
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "org.slf4j" % "slf4j-api" % "1.7.25"
)

lazy val otherDependencies = Seq(
  "io.spray" %% "spray-json" % "1.3.5"
)

libraryDependencies ++= (
  akkaDependencies++
  loggingDependencies++
  testDependencies++
  otherDependencies
  )