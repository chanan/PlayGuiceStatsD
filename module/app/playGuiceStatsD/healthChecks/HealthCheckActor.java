package playGuiceStatsD.healthChecks;

import java.util.Set;

import com.google.inject.Key;
import com.google.inject.name.Names;

import play.Logger;
import playGuiceStatsD.healthChecks.HealthCheck.Result;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

class HealthCheckActor extends UntypedActor {
	private final Set<Class<? extends HealthCheck>> checks;
	
	public HealthCheckActor(Set<Class<? extends HealthCheck>> checks) {
		this.checks = checks;
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof String) {
			for(Class<? extends HealthCheck> check : checks) {
				Props props = Props.create(Worker.class, check);
				ActorRef worker = getContext().actorOf(props);
				worker.tell("work", getSelf());
			}
		}
	}
	
	static class Worker extends UntypedActor {
		private final Class<? extends HealthCheck> healthCheckClass;
		
		public Worker(Class<? extends HealthCheck> healthCheckClass) {
			this.healthCheckClass = healthCheckClass;
		}
		
        @Override
        public void onReceive(Object message) throws Exception {
        	HealthCheck healthCheck = HealthChecker.Injector.getInstance(Key.get(HealthCheck.class, Names.named("PlayGuiceStatsD-HealthCheck-" + healthCheckClass.getName())));
			Result result = healthCheck.execute();
			if(result.isHealthy()) Logger.info(result.toString());
			else Logger.error(result.toString());
			getContext().stop(getSelf());
        }
	} 
}