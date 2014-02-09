package services;

import play.Logger;
import playGuiceStatsD.annotations.Counted;

public class SayHelloImpl implements SayHello {
	
	@Override
	@Counted
	public void Speak() {
		Logger.debug("Hello");
	}
}