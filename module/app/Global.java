import play.GlobalSettings;
import playGuiceStatsD.healthChecks.HealthChecker;

public class Global extends GlobalSettings {

	@Override
	public <A> A getControllerInstance(Class<A> clazz) throws Exception {
		return HealthChecker.getInjector().getInstance(clazz);
	}
}