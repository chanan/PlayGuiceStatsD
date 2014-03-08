package playGuiceStatsD.healthChecks;
import playGuiceStatsD.healthChecks.HealthCheck.Result;
import akka.actor.ActorRef;

class HealthCheckHolder {
	private final HealthCheck healthCheck;
	private final ActorRef actor;
	private Result result = null;
	
	public HealthCheckHolder(HealthCheck healthCheck, ActorRef actor) {
		this.healthCheck = healthCheck;
		this.actor = actor;
	}

	public HealthCheck getHealthCheck() {
		return healthCheck;
	}
	
	public ActorRef getActor() {
		return actor;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "HealthCheckHolder {healthCheck=" + healthCheck + ", actor=" + actor + ", result=" + result + "}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((actor == null) ? 0 : actor.hashCode());
		result = prime * result + ((healthCheck == null) ? 0 : healthCheck.hashCode());
		result = prime * result + ((this.result == null) ? 0 : this.result.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		HealthCheckHolder other = (HealthCheckHolder) obj;
		if (actor == null) {
			if (other.actor != null) return false;
		} else if (!actor.equals(other.actor)) return false;
		if (healthCheck == null) {
			if (other.healthCheck != null) return false;
		} else if (!healthCheck.equals(other.healthCheck)) return false;
		if (result == null) {
			if (other.result != null) return false;
		} else if (!result.equals(other.result)) return false;
		return true;
	}
}