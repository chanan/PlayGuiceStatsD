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
	private final String[] namespaces;
	
	public PlayGuiceStatsDModule(String... namespaces) {
		super();
		this.namespaces = namespaces;
	}
	@Override
	protected void configure() {
		Logger.debug("Play Guice StatsD Startup...");
		bindInterceptor(Matchers.nonActors(), Matchers.publicMethods(), new CountedAndTimedInterceptor());
		registerHealthChecks(binder());
	}

	private void registerHealthChecks(Binder binder) {
		Logger.debug("registerHealthChecks");
		final ConfigurationBuilder configBuilder = build(namespaces);
		final Reflections reflections = new Reflections(configBuilder.setScanners(new SubTypesScanner()));
		Set<Class<? extends HealthCheck>> checks = reflections.getSubTypesOf(HealthCheck.class);
		for(Class<? extends HealthCheck> check : checks) {
			Logger.debug("Heathcheck bound: " + check.getName());
			binder.bind(HealthCheck.class).annotatedWith(Names.named("PlayGuiceStatsD-HealthCheck-" + check.getName())).to(check);
		}
		binder.bind(ActorRef.class).annotatedWith(Names.named("PlayGuiceStatsD-HealthCheckActor")).toProvider(new ActorRefProvider(checks)).in(Singleton.class);
	}
	
	private static ConfigurationBuilder build(String... namespaces) {
		final ConfigurationBuilder configBuilder = new ConfigurationBuilder();
		for(final String namespace : namespaces) {
			configBuilder.addUrls(ClasspathHelper.forPackage(namespace));
		}
		return configBuilder;
	}
}