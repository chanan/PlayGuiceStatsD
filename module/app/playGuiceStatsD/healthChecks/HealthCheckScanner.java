package playGuiceStatsD.healthChecks;

import java.util.concurrent.TimeUnit;

import play.libs.Akka;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class HealthCheckScanner {
	private HealthCheckScanner() { }
	
	public static void Start(Injector injector) {
		ActorRef healthCheckActor = injector.getInstance(Key.get(ActorRef.class, Names.named("PlayGuiceStatsD-HealthCheckActor")));
		Akka.system().scheduler().schedule(
				Duration.apply(500, TimeUnit.MILLISECONDS),
				Duration.apply(5, TimeUnit.SECONDS),
				healthCheckActor,
				"tick",
				Akka.system().dispatcher(),
				null);
	}
}