package healthchecks;
import playGuiceStatsD.healthChecks.HealthCheck;

public class HealthCheckSimple extends HealthCheck {

	@Override
	protected Result check() throws Exception {
		return Result.healthy();
	}
}