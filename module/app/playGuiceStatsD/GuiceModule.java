package playGuiceStatsD;
import playGuiceStatsD.annotations.Counted;
import playGuiceStatsD.annotations.Timed;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

class GuiceModule extends AbstractModule {
	@Override
	protected void configure() {
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Counted.class), new CountedInterceptor());
		bindInterceptor(Matchers.any(), Matchers.annotatedWith(Timed.class), new TimedInterceptor());
	}
}