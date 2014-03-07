package playGuiceStatsD.healthChecks;
import playGuiceStatsD.healthChecks.HealthCheck.Result;

class HealthCheckResponse {
	private final Result result;
	private final Class<? extends HealthCheck> healthCheckClass;
	
	HealthCheckResponse(Result result, Class<? extends HealthCheck> healthCheckClass) {
		this.result = result;
		this.healthCheckClass = healthCheckClass;
	}
	
	Result getResult() {
		return result;
	}
	
	Class<? extends HealthCheck> getHealthCheckClass() {
		return healthCheckClass;
	}
}