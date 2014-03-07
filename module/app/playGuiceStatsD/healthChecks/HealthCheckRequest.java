package playGuiceStatsD.healthChecks;

class HealthCheckRequest {
	private Class<? extends HealthCheck> check;

	HealthCheckRequest(Class<? extends HealthCheck> check) {
		this.check = check;
	}
	
	Class<? extends HealthCheck> getCheck() {
		return check;
	}
}