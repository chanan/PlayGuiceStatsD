package healthchecks;

import playGuiceStatsD.healthChecks.HealthCheck;
import services.Session;

import com.google.inject.Inject;

public class HealthCheckComplex extends HealthCheck {

	private final Session session;
	
	@Inject
	public HealthCheckComplex(Session session) {
		this.session = session;
	}
	
	@Override
	protected Result check() throws Exception {
		if(session.getCountFromSomeQuery() > 0) return Result.healthy();
		else return Result.unhealthy("Count of the query should have returned results");
	}

}
