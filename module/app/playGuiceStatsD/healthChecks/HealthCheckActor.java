package playGuiceStatsD.healthChecks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import play.Logger;
import playGuiceStatsD.healthChecks.HealthCheck.Result;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.google.inject.Key;
import com.google.inject.name.Names;

class HealthCheckActor extends UntypedActor {
	private ActorRef sender;
	private final Set<Class<? extends HealthCheck>> checks;
	private Map<Class<? extends HealthCheck>, Result> responses = new HashMap<Class<? extends HealthCheck>, Result>();
	
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
		
		if(msg instanceof HealthChecksRequest) {
			responses.clear();
			sender = getSender();
			for(Class<? extends HealthCheck> check : checks) {
				Props props = Props.create(Worker.class);
				ActorRef worker = getContext().actorOf(props);
				HealthCheckRequest request = new HealthCheckRequest(check);
				responses.put(check, null);
				worker.tell(request, getSelf());
			}
		}
		
		if(msg instanceof HealthCheckResponse) {
			HealthCheckResponse response = (HealthCheckResponse) msg;
			responses.put(response.getHealthCheckClass(), response.getResult());
			if(isFull(responses)) {
				HealthCheckResponses healthCheckResponses = new HealthCheckResponses(new HashSet<Result>(responses.values()));
				sender.tell(healthCheckResponses, sender);
			}
		}
	}
	
	private boolean isFull(Map<Class<? extends HealthCheck>, Result> responses) {
		boolean isFull = true;
		for(Result result : responses.values()) {
			if(result == null) {
				isFull = false;
				break;
			}
		}
		return isFull;
	}

	static class Worker extends UntypedActor {
        @Override
        public void onReceive(Object message) throws Exception {
        	HealthCheckRequest request = (HealthCheckRequest) message;
        	HealthCheck healthCheck = HealthChecker.getInjector().getInstance(Key.get(HealthCheck.class, Names.named("PlayGuiceStatsD-HealthCheck-" + request.getCheck().getName())));
			Result result = healthCheck.execute();
			if(message instanceof String) {
				if(result.isHealthy()) Logger.info(result.toString());
				else Logger.error(result.toString());
			} else {
				HealthCheckResponse response = new HealthCheckResponse(result, request.getCheck());
				getSender().tell(response, getSelf());
			}
			getContext().stop(getSelf());
        }
	} 
}