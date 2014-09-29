name := "PlayGuiceStatsD"

version := "0.6.0"

libraryDependencies ++= Seq(
	"com.typesafe.play.plugins" %% "play-statsd" % "2.3.0",
	"com.google.inject" % "guice" % "3.0",
	"org.reflections" % "reflections" % "0.9.9-RC1"
)

publishTo <<= version { (v: String) =>
	if (v.trim.endsWith("SNAPSHOT"))
    	Some(Resolver.file("file",  new File( "../../maven-repo/snapshots" )) )
    else
    	Some(Resolver.file("file",  new File( "../../maven-repo/releases" )) )
} 

publishArtifact in(Compile, packageDoc) := false

publishMavenStyle := true

scalaVersion := "2.11.1"

lazy val main = project.in(file(".")).enablePlugins(PlayJava)