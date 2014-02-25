package services;

public class FakeCassandraSession implements Session {
	@Override
	public long getCountFromSomeQuery() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) { }
		return 5;
	}
}
