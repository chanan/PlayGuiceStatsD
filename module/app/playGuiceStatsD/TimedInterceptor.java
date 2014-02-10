package playGuiceStatsD;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import play.modules.statsd.Statsd;

class TimedInterceptor extends AbstractInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		final String statName = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
		final String combined = getCombinedPrefix() + ".time";
		
		final long start = System.currentTimeMillis();
		
		Object ret = null;
		boolean error = false;
		try {
			invocation.proceed();
		} catch (Exception e) {
			error = true;
			throw e;
		} finally {
			final long time = System.currentTimeMillis() - start;
			Statsd.timing(statName, time);
			Statsd.timing(combined, time);
			if(error) Statsd.timing(combined + ".error", time);
		}
		return ret;
	}
}