name := "scala_kafka_docker"

version := "0.1"

scalaVersion := "2.12.4"

val kafka = "com.typesafe.akka" %% "akka-stream-kafka" % "0.19"

val akka_http = Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.0-RC2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.9",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.0"
)

lazy val sign_up = (project in file("sign_up"))
  .enablePlugins(DockerPlugin)
  .settings(
    libraryDependencies ++=
      akka_http :+ kafka
  )

lazy val persistance = (project in file("persistance"))
  .enablePlugins(DockerPlugin)
  .settings(
    libraryDependencies ++= Seq(
      kafka
    )
  )