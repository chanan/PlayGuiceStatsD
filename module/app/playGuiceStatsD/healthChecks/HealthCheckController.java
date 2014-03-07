package playGuiceStatsD.healthChecks;

import static akka.pattern.Patterns.ask;
import play.Configuration;
import play.Play;
import play.libs.F.Function;
import play.libs.F.Promise;
import play.mvc.Controller;
import play.mvc.Result;
import akka.actor.ActorRef;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class HealthCheckController extends Controller {
	private final ActorRef healthCheckActor;
	private final Configuration config = Play.application().configuration();
	private final String return500OnErrorKey = "statsd.healthchecks.500OnError";

	@Inject
	public HealthCheckController(@Named("PlayGuiceStatsD-HealthCheckActor")ActorRef healthCheckActor) {
		this.healthCheckActor = healthCheckActor;
	}
	
	public Promise<Result> healthcheck() {
		return Promise.wrap(ask(healthCheckActor, new HealthChecksRequest(), 10000)).map(
			new Function<Object, Result>() {
				public Result apply(Object response) {
					HealthCheckResponses responses = (HealthCheckResponses) response;
					ViewModel vm = new ViewModel(responses.getResults());
					boolean return500OnError = true;
					if(config.getString(return500OnErrorKey) != null) return500OnError = config.getBoolean(return500OnErrorKey);
					if(return500OnError && !vm.isHealthy()) return internalServerError(views.html.healthcheck.render(vm));
					else return ok(views.html.healthcheck.render(vm));
				}
		});
	}
}