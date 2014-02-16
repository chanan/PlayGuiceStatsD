package services;

import play.Logger;

public class SayHelloImpl implements SayHello {
	
	@Override
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