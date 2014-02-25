import services.FakeCassandraSession;
import services.SayHello;
import services.SayHelloImpl;
import services.Session;

import com.google.inject.AbstractModule;

public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(SayHello.class).to(SayHelloImpl.class);
		bind(Session.class).to(FakeCassandraSession.class);
	}
}