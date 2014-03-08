package playGuiceStatsD.healthChecks;

import java.util.UUID;

class HealthCheckTimeoutRequest {

	private final UUID key;

	public HealthCheckTimeoutRequest(UUID key) {
		this.key = key;
	}

	public UUID getKey() {
		return key;
	}
}