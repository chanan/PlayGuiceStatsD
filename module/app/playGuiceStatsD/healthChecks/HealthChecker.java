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
		
		long initialDelay = 1;
		long interval = 5;
		TimeUnit timeUnitInitial = TimeUnit.MINUTES;
		TimeUnit timeUnitInterval = TimeUnit.MINUTES;
		if(config.getString("statsd.healthchecks.initialDelay") != null) {
			initialDelay = getTime(config.getString("statsd.healthchecks.initialDelay"));
			timeUnitInitial = getTimeUnit(config.getString("statsd.healthchecks.initialDelay"));
		}
		if(config.getString("statsd.healthchecks.interval") != null) {
			interval = getTime(config.getString("statsd.healthchecks.interval"));
			timeUnitInterval = getTimeUnit(config.getString("statsd.healthchecks.interval"));
		}
		
		ActorRef healthCheckActor = injector.getInstance(Key.get(ActorRef.class, Names.named("PlayGuiceStatsD-HealthCheckActor")));
		Akka.system().scheduler().schedule(
			Duration.apply(initialDelay, timeUnitInitial),
			Duration.apply(interval, timeUnitInterval),
			healthCheckActor,
			"tick",
			Akka.system().dispatcher(),
			null);
	}
	
	private static TimeUnit getTimeUnit(String duration) {
		String trimmed = duration.trim().toLowerCase();
		if(trimmed.endsWith("ns") || trimmed.endsWith("nanosecond") || trimmed.endsWith("nanoseconds")) return TimeUnit.NANOSECONDS;
		if(trimmed.endsWith("us") || trimmed.endsWith("microsecond") || trimmed.endsWith("microseconds")) return TimeUnit.MICROSECONDS;
		if(trimmed.endsWith("ms") || trimmed.endsWith("millisecond") || trimmed.endsWith("milliseconds")) return TimeUnit.MILLISECONDS;
		if(trimmed.endsWith("s") || trimmed.endsWith("second") || trimmed.endsWith("seconds")) return TimeUnit.SECONDS;
		if(trimmed.endsWith("m") || trimmed.endsWith("minute") || trimmed.endsWith("minutes")) return TimeUnit.MINUTES;
		if(trimmed.endsWith("h") || trimmed.endsWith("hour") || trimmed.endsWith("hours")) return TimeUnit.HOURS;
		if(trimmed.endsWith("d") || trimmed.endsWith("day") || trimmed.endsWith("days")) return TimeUnit.DAYS;
		else return TimeUnit.MILLISECONDS;
	}
	
	//TODO: Typesafe config has a getDuration in newer version, use that when play is updated
	private static long getTime(String duration) {
		String trimmed = duration.trim();
		String number = "";
		for(char ch : trimmed.toCharArray()) {
			if(Character.isDigit(ch)) number = number + ch;
			else break;
		}
		return Long.parseLong(number);
	}
	
	public static Injector getInjector() {
		return Injector;
	}
}