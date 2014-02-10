package services;

import play.Logger;
import playGuiceStatsD.annotations.Counted;
import playGuiceStatsD.annotations.Timed;

public class SayHelloImpl implements SayHello {
	
	@Override
	@Timed
	@Counted
	public void Speak() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Logger.debug("Hello");
	}

	@SuppressWarnings("unused")
	@Override
	@Counted
	@Timed
	public void Error() {
		try {
			Thread.sleep(750);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		double x = 2 / 0;
		Logger.error("The above line should have caused an exception");
	}
}