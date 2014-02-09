package services;

import play.Logger;
import playGuiceStatsD.annotations.Counted;
import playGuiceStatsD.annotations.Timed;

public class SayHelloImpl implements SayHello {
	
	@Override
	@Counted
	@Timed
	public void Speak() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Logger.debug("Hello");
	}
}