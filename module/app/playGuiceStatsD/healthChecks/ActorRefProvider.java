package playGuiceStatsD.healthChecks;
import java.util.Set;

import play.libs.Akka;
import akka.actor.ActorRef;
import akka.actor.Props;

import com.google.inject.Provider;

public class ActorRefProvider implements Provider<ActorRef> {
	private final Set<Class<? extends HealthCheck>> checks;
	
	public ActorRefProvider(Set<Class<? extends HealthCheck>> checks) {
		this.checks = checks;
	}
	
	@Override
	public ActorRef get() {
		return Akka.system().actorOf(Props.create(HealthCheckActor.class, checks));
	}
}