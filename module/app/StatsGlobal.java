import play.GlobalSettings;
import playGuiceStatsD.healthChecks.HealthChecker;

public class StatsGlobal extends GlobalSettings {

	@Override
	public <A> A getControllerInstance(Class<A> clazz) throws Exception {
		return HealthChecker.getInjector().getInstance(clazz);
	}
}