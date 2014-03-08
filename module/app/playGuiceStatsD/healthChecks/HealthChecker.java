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
	private final static String statsEnabled = "statsd.enabled";
	private final static String checksEnabled = "statsd.healthchecks.enabled";
	private final static String initialDelayKey = "statsd.healthchecks.initialDelay";
	private final static String intervalKey = "statsd.healthchecks.interval";
	
	private static Injector Injector;
	
	public static void Start(Injector injector) {
		Injector = injector;
		if(!config.getBoolean(statsEnabled)) return;
		if(!config.getBoolean(checksEnabled)) return;
		
		long initialDelay = 60000;
		long interval = 300000;
		if(config.getString(initialDelayKey) != null) {
			initialDelay = config.getMilliseconds(initialDelayKey);
		}
		if(config.getString(intervalKey) != null) {
			interval = config.getMilliseconds(intervalKey);
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