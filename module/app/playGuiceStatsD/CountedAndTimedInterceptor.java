package playGuiceStatsD;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import play.modules.statsd.Statsd;

class CountedAndTimedInterceptor extends AbstractInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		if(!isEnabled()) return invocation.proceed();
		
		final String statName = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
		final String combinedCount = getCombinedPrefix() + ".count";
		final String combinedTime = getCombinedPrefix() + ".time";
		
		Statsd.increment(statName);
		Statsd.increment(combinedCount);
	
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
			Statsd.timing(combinedTime, time);
			if(error) {
				Statsd.timing(combinedTime + ".error", time);
				Statsd.increment(combinedCount + ".error");
			}
		}
		return ret;
	}
}