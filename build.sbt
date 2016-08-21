organization := "com.rxthings"
name := "akka-injects"
version := "0.5"
licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

scalaVersion := "2.11.8"
scalacOptions += "-target:jvm-1.8"

resolvers += "jw3 at bintray" at "https://dl.bintray.com/jw3/maven"

libraryDependencies ++= {
    val akkaVersion = "2.4.9"
    val scalatestVersion = "3.0.0"

    Seq(
        "com.iheart" %% "ficus" % "1.2.6",
        "net.codingwell" %% "scala-guice" % "4.1.0",

        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion % Runtime,
        "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",

        "org.scalactic" %% "scalactic" % scalatestVersion % Test,
        "org.scalatest" %% "scalatest" % scalatestVersion % Test,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
    )
}

com.updateimpact.Plugin.apiKey in ThisBuild := sys.env.getOrElse("UPDATEIMPACT_API_KEY", (com.updateimpact.Plugin.apiKey in ThisBuild).value)
