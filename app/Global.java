import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import playGuiceStatsD.PlayGuiceStatsD;

import com.google.inject.Injector;

public class Global extends GlobalSettings {
	private Injector injector = PlayGuiceStatsD.CreateInjector();
	
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
		injector = injector.createChildInjector(new GuiceModule());
	}	
}