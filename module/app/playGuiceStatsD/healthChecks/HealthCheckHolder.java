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
}