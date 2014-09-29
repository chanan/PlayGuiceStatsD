name := "PlayGuiceStatsDSample"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
	"com.typesafe.play.plugins" %% "play-statsd" % "2.3.0",
	"com.google.inject" % "guice" % "3.0",
	"akkaguice" %% "akkaguice" % "0.8.0"
)

scalaVersion := "2.11.1"

lazy val module = project.in(file("module")).enablePlugins(PlayJava)

lazy val main = project.in(file(".")).enablePlugins(PlayJava).dependsOn(module).aggregate(module)

resolvers += "release repository" at "http://chanan.github.io/maven-repo/releases/"

resolvers += "snapshot repository" at "http://chanan.github.io/maven-repo/snapshots/"