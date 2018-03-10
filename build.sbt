name := "scala_kafka_docker"

version := "0.1"

scalaVersion := "2.12.4"

val kafka = "com.typesafe.akka" %% "akka-stream-kafka" % "0.19"

val akka_http = Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.0-RC2",
  "com.typesafe.akka" %% "akka-stream" % "2.5.9"
)

val slick = Seq(
  "com.typesafe.slick" %% "slick" % "3.2.2",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.2"
)

val jsonParser = "org.json4s" %% "json4s-jackson" % "3.6.0-M2"

val bCrypt = "org.mindrot" % "jbcrypt" % "0.4"

val dockerSettings = Seq(

  dockerfile in docker := {
    val artifactSource: File = assembly.value
    val artifactTargetPath = s"/project/${artifactSource.name}"
    val projectDir = "/project/"
    val scriptSourceDir = baseDirectory.value / "../script"

    new Dockerfile {
      from("openjdk:8-jre")
      add(artifactSource, artifactTargetPath)
      copy(scriptSourceDir, projectDir)
      entryPoint(s"/project/start.sh")
      cmd(projectDir, s"${name.value}", s"${version.value}")
    }
  },
  imageNames in docker := Seq(
    ImageName(s"${name.value}:latest")
  )
)

lazy val sign_up = (project in file("sign_up"))
  .enablePlugins(DockerPlugin)
  .settings(
    libraryDependencies ++=
      akka_http :+ kafka :+ jsonParser,
    dockerSettings
  )

lazy val persistance = (project in file("persistance"))
  .enablePlugins(DockerPlugin)
  .settings(
    libraryDependencies ++=
      slick :+ kafka :+ bCrypt :+ jsonParser,
    dockerSettings
  )