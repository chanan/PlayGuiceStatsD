package playGuiceStatsD.healthChecks;
import java.util.UUID;

class HealthCheckRequest {
	private final HealthCheck healthCheck;
	private final UUID key;

	HealthCheckRequest(UUID key, HealthCheck healthCheck) {
		this.key = key;
		this.healthCheck = healthCheck;
	}
	
	HealthCheck getCheck() {
		return healthCheck;
	}
	
	UUID getKey() {
		return key;
	}
}