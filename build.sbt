
enablePlugins(GitVersioning)

organization := "com.github.jw3"
name := "akka-injects"
git.useGitDescribe := true
licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))

crossScalaVersions := Seq("2.11.8", "2.12.6")

scalaVersion := "2.12.6"

scalacOptions ++= Seq(
    "-target:jvm-1.8",
    "-encoding", "UTF-8",

    "-feature",
    "-unchecked",
    "-deprecation",

    "-language:postfixOps",
    "-language:implicitConversions",

    "-Ywarn-unused-import",
    "-Xfatal-warnings",
    "-Xlint:_"
)

resolvers ++= Seq(
    Resolver.bintrayRepo("jw3", "maven"),
    Resolver.jcenterRepo
)

libraryDependencies ++= {
    val akkaVersion = "2.5.14"
    val scalatestVersion = "3.0.3"

    Seq(
        "com.iheart" %% "ficus" % "1.4.0",
        "net.codingwell" %% "scala-guice" % "4.2.0",

        "com.typesafe.akka" %% "akka-actor" % akkaVersion,
        "com.typesafe.akka" %% "akka-slf4j" % akkaVersion % Runtime,
        "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",

        "org.scalactic" %% "scalactic" % scalatestVersion % Test,
        "org.scalatest" %% "scalatest" % scalatestVersion % Test,
        "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
    )
}

com.updateimpact.Plugin.apiKey in ThisBuild := sys.env.getOrElse("UPDATEIMPACT_API_KEY", (com.updateimpact.Plugin.apiKey in ThisBuild).value)
