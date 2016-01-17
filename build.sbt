organization := "com.rxthings"
name := "akka-injects"
version := "0.3-SNAPSHOT"
licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

scalaVersion := "2.11.5"
scalacOptions += "-target:jvm-1.8"

resolvers += "jw3 at bintray" at "https://dl.bintray.com/jw3/maven"

libraryDependencies ++= {
    val akkaVersion = "2.4.0"

    Seq(
        "org.scala-lang" % "scala-reflect" % "2.11.5",
        "net.codingwell" %% "scala-guice" % "4.0.1",
        "com.google.inject" % "guice" % "4.0",

        "com.typesafe" % "config" % "1.3.0",
        "net.ceedubs" %% "ficus" % "1.1.2",

        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion % Runtime,

        "org.scalatest" %% "scalatest" % "2.2.5" % Test,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
    )
}

ScoverageSbtPlugin.ScoverageKeys.coverageMinimum := 60

ScoverageSbtPlugin.ScoverageKeys.coverageFailOnMinimum := false
