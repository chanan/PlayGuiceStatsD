import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import playGuiceStatsD.PlayGuiceStatsDModule;
import playGuiceStatsD.healthChecks.HealthChecker;
import akkaGuice.AkkaGuice;
import akkaGuice.AkkaGuiceModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Global extends GlobalSettings {
	private final Injector injector = Guice.createInjector(new PlayGuiceStatsDModule("healthchecks"), new AkkaGuiceModule("services"), new GuiceModule());
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends EssentialFilter> Class<T>[] filters() {
		return new Class[] {play.modules.statsd.StatsdFilter.class};
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) throws Exception {
		return injector.getInstance(clazz);
	}

	@Override
	public void onStart(Application arg0) {
		AkkaGuice.InitializeInjector(injector, "services");
		HealthChecker.Start(injector);
	}	
}