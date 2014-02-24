package playGuiceStatsD;
import play.Configuration;
import play.Play;

abstract class AbstractInterceptor {
	private static Configuration config;
	private final static String defaultValue = "guice.combined";
	private final static String configKey = "statsd.guice.combined.prefix";
	private String combined = null;
	
	String getCombinedPrefix() {
		if(combined != null) return combined;
		if(getConfig().getString(configKey) != null) {
			combined = config.getString(configKey);
		} else {
			combined = defaultValue;
		}
		return combined;
	}
	
	boolean isEnabled() {
		return getConfig().getBoolean("statsd.enabled");
	}
	
	Configuration getConfig() {
		if(config == null) config = Play.application().configuration();
		return config;
	}
}