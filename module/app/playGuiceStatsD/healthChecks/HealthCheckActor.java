package playGuiceStatsD.healthChecks;

import java.util.Set;

import play.Logger;
import playGuiceStatsD.healthChecks.HealthCheck.Result;
import akka.actor.UntypedActor;

public class HealthCheckActor extends UntypedActor {
	private final Set<Class<? extends HealthCheck>> checks;
	
	public HealthCheckActor(Set<Class<? extends HealthCheck>> checks) {
		this.checks = checks;
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof String) {
			String message = (String)msg;
			if(message.equalsIgnoreCase("check")) {
				for(Class<? extends HealthCheck> check : checks) {
					HealthCheck healthCheck = check.newInstance();
					getSender().tell(healthCheck.execute().toString(), getSelf());
				}
			} else if(message.equalsIgnoreCase("tick")) {
				Logger.debug("Checking health...");
				for(Class<? extends HealthCheck> check : checks) {
					HealthCheck healthCheck = check.newInstance();
					Result result = healthCheck.execute();
					if(result.isHealthy()) Logger.debug(result.toString());
					else Logger.error(result.toString());
				}
			}
		}
	}
}