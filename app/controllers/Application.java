package controllers;
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
	}
	
    public Result index() {
    	sayHello.Speak();
    	actor.tell("tick", null);
        return ok(index.render("Your new application is ready."));
    }
    
    public Result causeError() {
    	sayHello.Error();
    	return ok("An error should have occured. This won't print");
    }
}