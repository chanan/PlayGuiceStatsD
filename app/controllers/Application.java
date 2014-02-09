package controllers;
import play.mvc.Controller;
import play.mvc.Result;
import services.SayHello;
import views.html.index;

import com.google.inject.Inject;

public class Application extends Controller {
	private final SayHello sayHello;
	
	@Inject
	public Application(SayHello sayHello) {
		this.sayHello = sayHello;
	}
	
    public Result index() {
    	sayHello.Speak();
        return ok(index.render("Your new application is ready."));
    }
}