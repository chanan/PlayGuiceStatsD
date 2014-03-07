package playGuiceStatsD.healthChecks;
import java.util.Set;

import playGuiceStatsD.healthChecks.HealthCheck.Result;

class HealthCheckResponses {
	private final Set<Result> results;
	
	HealthCheckResponses(Set<Result> results) {
		this.results = results;
	}
	
	public Set<Result> getResults() {
		return results;
	}
}