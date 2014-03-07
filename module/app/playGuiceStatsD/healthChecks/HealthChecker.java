package playGuiceStatsD.healthChecks;
import java.util.concurrent.TimeUnit;

import play.Configuration;
import play.Play;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;

public class HealthChecker {
	private HealthChecker() { }
	private final static Configuration config = Play.application().configuration();
	
	private static Injector Injector;
	
	public static void Start(Injector injector) {
		Injector = injector;
		if(!config.getBoolean("statsd.enabled")) return;
		if(!config.getBoolean("statsd.healthchecks.enabled")) return;
		
		long initialDelay = 60000;
		long interval = 300000;
		if(config.getString("statsd.healthchecks.initialDelay") != null) {
			initialDelay = config.getMilliseconds("statsd.healthchecks.initialDelay");
		}
		if(config.getString("statsd.healthchecks.interval") != null) {
			interval = config.getMilliseconds("statsd.healthchecks.interval");
		}
		
		ActorRef healthCheckActor = injector.getInstance(Key.get(ActorRef.class, Names.named("PlayGuiceStatsD-HealthCheckActor")));
		Akka.system().scheduler().schedule(
			Duration.apply(initialDelay, TimeUnit.MILLISECONDS),
			Duration.apply(interval, TimeUnit.MILLISECONDS),
			healthCheckActor,
			new HealthCheckTimedRequest(),
			Akka.system().dispatcher(),
			null);
	}
	
	public static Injector getInjector() {
		return Injector;
	}
}