package playGuiceStatsD.healthChecks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import play.Configuration;
import play.Logger;
import play.Play;
import play.libs.Akka;
import playGuiceStatsD.healthChecks.HealthCheck.Result;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

import com.google.inject.Key;
import com.google.inject.name.Names;

class HealthCheckActor extends UntypedActor {
	private ActorRef sender;
	private final Set<Class<? extends HealthCheck>> checks;
	private final Map<Class<? extends HealthCheck>, HealthCheckHolder> responses = new HashMap<Class<? extends HealthCheck>, HealthCheckHolder>();
	private final Configuration config = Play.application().configuration();
	
	public HealthCheckActor(Set<Class<? extends HealthCheck>> checks) {
		this.checks = checks;
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof HealthCheckTimedRequest) {
			sender = null;
			startCheck();
		}
		
		if(msg instanceof HealthChecksRequest) {
			sender = getSender();
			startCheck();
		}
		
		if(msg instanceof HealthCheckResponse) {
			if (responses.isEmpty()) return;
			HealthCheckResponse response = (HealthCheckResponse) msg;
			responses.get(response.getHealthCheckClass()).setResult(response.getResult());
			if(response.getResult().isHealthy()) Logger.info(response.getResult().toString());
			else Logger.error(response.getResult().toString());
			if(sender != null && isFull(responses)) {
				HealthCheckResponses healthCheckResponses = new HealthCheckResponses(getResultSet(responses));
				sender.tell(healthCheckResponses, sender);
				responses.clear();
			};
		}
		
		if(msg instanceof HealthCheckTimeoutRequest) {
			if (responses.isEmpty()) return;
			for(HealthCheckHolder holder : responses.values()) {
				if(holder.getResult() == null) {
					Result result = holder.getHealthCheck().timeout(config.getMilliseconds("statsd.healthchecks.timeout"));
					Logger.error(result.toString());
					holder.setResult(result);
				}
			}
			HealthCheckResponses healthCheckResponses = new HealthCheckResponses(getResultSet(responses));
			sender.tell(healthCheckResponses, sender);
			responses.clear();
		}
	}

	private void startCheck() {
		responses.clear();
		for(Class<? extends HealthCheck> check : checks) {
			Props props = Props.create(Worker.class);
			ActorRef worker = getContext().actorOf(props);
			HealthCheck healthCheck = HealthChecker.getInjector().getInstance(Key.get(HealthCheck.class, Names.named("PlayGuiceStatsD-HealthCheck-" + check.getName())));
			HealthCheckRequest request = new HealthCheckRequest(healthCheck);
			responses.put(check, new HealthCheckHolder(healthCheck, worker));
			worker.tell(request, getSelf());
		}
		startTimer();
	}
	
	private void startTimer() {
		if(config.getString("statsd.healthchecks.timeout") == null) return;
		long interval = config.getMilliseconds("statsd.healthchecks.timeout");
		Akka.system().scheduler().scheduleOnce( 
			Duration.apply(interval, TimeUnit.MILLISECONDS),
			getSelf(),
			new HealthCheckTimeoutRequest(),
			Akka.system().dispatcher(),
			getSelf());
	}
	
	private Set<Result> getResultSet(Map<Class<? extends HealthCheck>, HealthCheckHolder> responses) {
		Set<Result> resultSet = new HashSet<Result>();
		for(HealthCheckHolder holder : responses.values()) {
			resultSet.add(holder.getResult());
		}
		return resultSet;
	}
	
	private boolean isFull(Map<Class<? extends HealthCheck>, HealthCheckHolder> responses) {
		boolean isFull = true;
		for(HealthCheckHolder holder : responses.values()) {
			if(holder.getResult() == null) {
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
        	HealthCheck healthCheck = request.getCheck();
			Result result = healthCheck.execute();
			HealthCheckResponse response = new HealthCheckResponse(result, healthCheck.getClass());
			getSender().tell(response, getSelf());
			getContext().stop(getSelf());
        }
	} 
}