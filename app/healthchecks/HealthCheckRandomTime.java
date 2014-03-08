package healthchecks;
import java.util.Random;

import playGuiceStatsD.healthChecks.HealthCheck;

public class HealthCheckRandomTime extends HealthCheck {

	@Override
	protected Result check() throws Exception {
		Random rnd = new Random();
		if(rnd.nextBoolean()) {
			Thread.sleep(500);
			return Result.healthy("Random time: 500ms");
		}
		else {
			Thread.sleep(10000);
			return Result.healthy("Random time: 10,000ms");
		}
	}
}