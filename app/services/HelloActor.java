package services;
import play.Logger;
import akka.actor.UntypedActor;

import com.google.inject.Inject;

public class HelloActor extends UntypedActor {
	private final SayHello sayHello;
	
	@Inject
	public HelloActor(SayHello sayHello) {
		this.sayHello = sayHello;
		Logger.debug(sayHello.toString());
	}

	@Override
	public void onReceive(Object arg0) throws Exception {
		Logger.debug("Hello from Actor");
		sayHello.Speak();
	}
}