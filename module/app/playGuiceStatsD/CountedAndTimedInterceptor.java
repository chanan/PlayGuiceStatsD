package playGuiceStatsD;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

class CountedAndTimedInterceptor extends AbstractInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if(!isEnabled()) return invocation.proceed();
		
		final String statName = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
		Statsd.increment(statName);
		final long start = System.currentTimeMillis();
		boolean error = false;
		Object ret = null;
		try {
			ret = invocation.proceed();
		} catch (Exception e) {
			error = true;
			throw e;
		} finally {
			final long time = System.currentTimeMillis() - start;
			Statsd.timing(statName, time);
			if(error) {
				Statsd.timing(statName + ".error", time);
				Statsd.increment(statName + ".error");
			}
		}
		return ret;
	}
}