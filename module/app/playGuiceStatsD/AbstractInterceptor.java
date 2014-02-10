package playGuiceStatsD;
import play.Configuration;
import play.Play;

public abstract class AbstractInterceptor {
	private final static String defaultValue = "guice.combined";
	private final static String configKey = "statsd.guice.combined.prefix";
	private String combined = null;
	
	String getCombinedPrefix() {
		if(combined != null) return combined;
		final Configuration config = Play.application().configuration();
		if(config.getString(configKey) != null) {
			combined = config.getString(configKey);
		} else {
			combined = defaultValue;
		}
		return combined;
	}
}