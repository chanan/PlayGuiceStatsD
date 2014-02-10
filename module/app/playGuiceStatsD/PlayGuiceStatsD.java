package playGuiceStatsD;

import play.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class PlayGuiceStatsD {
	public static Injector CreateInjector() {
		Logger.debug("Play Guice StatsD Startup...");
		return Guice.createInjector(new GuiceModule());
	}
}