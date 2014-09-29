package controllers;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import services.SayHello;
import views.html.index;
import akka.actor.ActorRef;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class Application extends Controller {
	private final SayHello sayHello;
	private final ActorRef actor;
	
	@Inject
	public Application(SayHello sayHello, @Named("HelloActor") ActorRef actor) {
		this.sayHello = sayHello;
		this.actor = actor;
		Logger.debug("SayHello from constructor: " + sayHello.toString());
	}
	
    public Result index() {
        Logger.debug("SayHello from action (on next line)");
    	sayHello.Speak();
    	actor.tell("tick", null);
        return ok(index.render());
    }
    
    public Result causeError() {
    	sayHello.Error();
    	return ok("An error should have occured. This won't print");
    }
}