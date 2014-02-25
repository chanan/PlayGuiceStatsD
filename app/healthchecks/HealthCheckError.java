package healthchecks;
import playGuiceStatsD.healthChecks.HealthCheck;

public class HealthCheckError extends HealthCheck {

	@Override
	protected Result check() throws Exception {
		Thread.sleep(1000);
		return Result.unhealthy(new Exception("Error"));
	}
}