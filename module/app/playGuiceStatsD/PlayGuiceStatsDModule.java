package playGuiceStatsD;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import play.Logger;
import playGuiceStatsD.healthChecks.ActorRefProvider;
import playGuiceStatsD.healthChecks.HealthCheck;
import akka.actor.ActorRef;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Singleton;
import com.google.inject.name.Names;

public class PlayGuiceStatsDModule extends AbstractModule {
	@Override
	protected void configure() {
		Logger.debug("Play Guice StatsD Startup...");
		bindInterceptor(Matchers.nonActors(), Matchers.publicMethods(), new CountedAndTimedInterceptor());
		registerHealthChecks(binder());
	}

	private void registerHealthChecks(Binder binder) {
		Logger.debug("registerHealthChecks");
		final Reflections reflections = new Reflections(new ConfigurationBuilder()
			.setUrls(ClasspathHelper.forPackage("healthChecks"))
			.setScanners(new SubTypesScanner()));
		Set<Class<? extends HealthCheck>> checks = reflections.getSubTypesOf(HealthCheck.class);
		Logger.debug("HealthChecks: " + checks);
		binder.bind(ActorRef.class).annotatedWith(Names.named("PlayGuiceStatsD-HealthCheckActor")).toProvider(new ActorRefProvider(checks)).in(Singleton.class);
	}
}