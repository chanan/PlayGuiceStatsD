package playGuiceStatsD.healthChecks;
import java.util.Set;

import playGuiceStatsD.healthChecks.HealthCheck.Result;

public class ViewModel {
	private final Set<HealthCheck.Result> results;

	public ViewModel(Set<Result> results) {
		this.results = results;
	}

	public Set<HealthCheck.Result> getResults() {
		return results;
	}
	
	public boolean isHealthy() {
		boolean ret = true;
		for(HealthCheck.Result result : results) {
			if(!result.isHealthy()) {
				ret = false;
				break;
			}
		}
		return ret;
	}
}