package playGuiceStatsD.healthChecks;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
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
	//private ActorRef sender;
	private final Set<Class<? extends HealthCheck>> checks;
	//private final Map<Class<? extends HealthCheck>, HealthCheckHolder> responses = new HashMap<Class<? extends HealthCheck>, HealthCheckHolder>();
	private final Map<UUID, HealthCheckSession> sessions = new HashMap<UUID, HealthCheckSession>();
	private final Configuration config = Play.application().configuration();
	private final boolean useTimeout = config.getString("statsd.healthchecks.timeout") != null;
	private final long timeout;
	
	public HealthCheckActor(Set<Class<? extends HealthCheck>> checks) {
		this.checks = checks;
		if(useTimeout) timeout = config.getMilliseconds("statsd.healthchecks.timeout");
		else timeout = 0;
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		if(msg instanceof HealthCheckTimedRequest) {
			UUID key = UUID.randomUUID();
			HealthCheckSession session = new HealthCheckSession(key);
			sessions.put(key, session);
			startCheck(session);
		}
		
		if(msg instanceof HealthChecksRequest) {
			UUID key = UUID.randomUUID();
			HealthCheckSession session = new HealthCheckSession(key, getSender());
			sessions.put(key, session);
			startCheck(session);
		}
		
		if(msg instanceof HealthCheckResponse) {
			HealthCheckResponse response = (HealthCheckResponse) msg;
			if (!sessions.containsKey(response.getKey())) return;
			sessions.get(response.getKey()).getHolder(response.getHealthCheckClass()).setResult(response.getResult());
			if(response.getResult().isHealthy()) Logger.info(response.getResult().toString());
			else Logger.error(response.getResult().toString());
			ActorRef sender = sessions.get(response.getKey()).getSender();
			if(sender != null && isFull(sessions.get(response.getKey()).getHolders())) {
				HealthCheckResponses healthCheckResponses = new HealthCheckResponses(getResultSet(sessions.get(response.getKey()).getHolders()));
				sender.tell(healthCheckResponses, sender);
				sessions.remove(response.getKey());
			};
		}
		
		if(msg instanceof HealthCheckTimeoutRequest) {
			HealthCheckTimeoutRequest request = (HealthCheckTimeoutRequest) msg;
			if (!sessions.containsKey(request.getKey())) return;
			for(HealthCheckHolder holder : sessions.get(request.getKey()).getHolders()) {
				if(holder.getResult() == null) {
					Result result = holder.getHealthCheck().timeout(timeout);
					Logger.error(result.toString());
					holder.setResult(result);
				}
			}
			HealthCheckResponses healthCheckResponses = new HealthCheckResponses(getResultSet(sessions.get(request.getKey()).getHolders()));
			ActorRef sender = sessions.get(request.getKey()).getSender();
			if(sender != null) sender.tell(healthCheckResponses, sender);
			sessions.remove(request.getKey());
		}
	}

	private void startCheck(HealthCheckSession session) {
		for(Class<? extends HealthCheck> check : checks) {
			Props props = Props.create(Worker.class);
			ActorRef worker = getContext().actorOf(props);
			HealthCheck healthCheck = HealthChecker.getInjector().getInstance(Key.get(HealthCheck.class, Names.named("PlayGuiceStatsD-HealthCheck-" + check.getName())));
			HealthCheckRequest request = new HealthCheckRequest(session.getKey(), healthCheck);
			session.addHolder(check, new HealthCheckHolder(healthCheck, worker));
			worker.tell(request, getSelf());
		}
		startTimer(session);
	}
	
	private void startTimer(HealthCheckSession session) {
		if(!useTimeout) return;
		Akka.system().scheduler().scheduleOnce( 
			Duration.apply(timeout, TimeUnit.MILLISECONDS),
			getSelf(),
			new HealthCheckTimeoutRequest(session.getKey()),
			Akka.system().dispatcher(),
			getSelf());
	}
	
	private Set<Result> getResultSet(Collection<HealthCheckHolder> holders) {
		Set<Result> resultSet = new HashSet<Result>();
		for(HealthCheckHolder holder : holders) {
			resultSet.add(holder.getResult());
		}
		return resultSet;
	}
	
	private boolean isFull(Collection<HealthCheckHolder> holders) {
		boolean isFull = true;
		for(HealthCheckHolder holder : holders) {
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
			HealthCheckResponse response = new HealthCheckResponse(request.getKey(), result, healthCheck.getClass());
			getSender().tell(response, getSelf());
			getContext().stop(getSelf());
        }
	} 
}