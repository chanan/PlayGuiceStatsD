package playGuiceStatsD;
import play.Logger;

import com.google.inject.AbstractModule;

public class PlayGuiceStatsDModule extends AbstractModule {
	@Override
	protected void configure() {
		Logger.debug("Play Guice StatsD Startup...");
		bindInterceptor(Matchers.nonActors(), Matchers.publicMethods(), new CountedAndTimedInterceptor());
	}
}