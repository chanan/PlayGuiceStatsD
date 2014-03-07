package playGuiceStatsD.healthChecks;

class HealthCheckRequest {
	private HealthCheck healthCheck;

	HealthCheckRequest(HealthCheck healthCheck) {
		this.healthCheck = healthCheck;
	}
	
	HealthCheck getCheck() {
		return healthCheck;
	}
}