package playGuiceStatsD.healthChecks;
import java.util.UUID;

import playGuiceStatsD.healthChecks.HealthCheck.Result;

class HealthCheckResponse {
	private final Result result;
	private final Class<? extends HealthCheck> healthCheckClass;
	private final UUID key;
	
	HealthCheckResponse(UUID key, Result result, Class<? extends HealthCheck> healthCheckClass) {
		this.key = key;
		this.result = result;
		this.healthCheckClass = healthCheckClass;
	}
	
	UUID getKey() {
		return key;
	}
	
	Result getResult() {
		return result;
	}
	
	Class<? extends HealthCheck> getHealthCheckClass() {
		return healthCheckClass;
	}
}