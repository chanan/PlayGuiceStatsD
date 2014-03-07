package playGuiceStatsD.healthChecks;
import playGuiceStatsD.healthChecks.HealthCheck.Result;

class HealthCheckHolder {
	private final HealthCheck healthCheck;
	private Result result = null;;
	
	public HealthCheckHolder(HealthCheck healthCheck) {
		this.healthCheck = healthCheck;
	}

	public HealthCheck getHealthCheck() {
		return healthCheck;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}
}