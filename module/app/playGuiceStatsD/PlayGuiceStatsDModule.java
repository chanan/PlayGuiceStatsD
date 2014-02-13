package playGuiceStatsD;
import play.Logger;
import playGuiceStatsD.annotations.Counted;
import playGuiceStatsD.annotations.Timed;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public class PlayGuiceStatsDModule extends AbstractModule {
	@Override
	protected void configure() {
		Logger.debug("Play Guice StatsD Startup...");
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Counted.class), new CountedInterceptor());
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Timed.class), new TimedInterceptor());
		bindInterceptor(Matchers.annotatedWith(Counted.class), PublicMethodMatcher.publicMethods(), new CountedInterceptor());
		bindInterceptor(Matchers.annotatedWith(Timed.class), PublicMethodMatcher.publicMethods(), new TimedInterceptor());
	}
}