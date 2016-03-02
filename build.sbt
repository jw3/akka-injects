organization := "com.rxthings"
name := "akka-injects"
version := "0.4-SNAPSHOT"
licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

scalaVersion := "2.11.7"
scalacOptions += "-target:jvm-1.8"

resolvers += "jw3 at bintray" at "https://dl.bintray.com/jw3/maven"

libraryDependencies ++= {
    val akkaVersion = "2.4.2"

    Seq(
        "org.scala-lang" % "scala-reflect" % "2.11.7",
        "net.codingwell" %% "scala-guice" % "4.0.1",
        "com.google.inject" % "guice" % "4.0",

        "com.typesafe" % "config" % "1.3.0",
        "net.ceedubs" %% "ficus" % "1.1.2",

        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion % Runtime,
        "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",

        "org.scalatest" %% "scalatest" % "2.2.5" % Test,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
    )
}

com.updateimpact.Plugin.apiKey in ThisBuild := sys.env.getOrElse("UPDATEIMPACT_API_KEY", (com.updateimpact.Plugin.apiKey in ThisBuild).value)
