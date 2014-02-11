package services;
import akka.actor.UntypedActor;

import com.google.inject.Inject;
import akkaGuice.annotations.RegisterActor;

@RegisterActor
public class HelloActor extends UntypedActor {
	private final SayHello sayHello;
	
	@Inject
	public HelloActor(SayHello sayHello) {
		this.sayHello = sayHello;
	}

	@Override
	public void onReceive(Object arg0) throws Exception {
		sayHello.Speak();
	}
}