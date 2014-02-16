name := "PlayGuiceStatsD"

version := "0.2.0"

libraryDependencies ++= Seq(
	"com.typesafe.play.plugins" %% "play-statsd" % "2.2.0",
	"com.google.inject" % "guice" % "3.0"
)

publishTo <<= version { (v: String) =>
	if (v.trim.endsWith("SNAPSHOT"))
    	Some(Resolver.file("file",  new File( "../../maven-repo/snapshots" )) )
    else
    	Some(Resolver.file("file",  new File( "../../maven-repo/releases" )) )
} 

publishArtifact in(Compile, packageDoc) := false

publishMavenStyle := true 

play.Project.playJavaSettings