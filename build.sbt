name := "PlayGuiceStatsDSample"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
	"com.typesafe.play.plugins" %% "play-statsd" % "2.2.0",
	"com.google.inject" % "guice" % "3.0",
	"akkaguice" %% "akkaguice" % "0.6.0"
)     

play.Project.playJavaSettings

lazy val module = project.in(file("module"))

lazy val main = project.in(file(".")).dependsOn(module).aggregate(module)