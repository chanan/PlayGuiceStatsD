package playGuiceStatsD;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import play.modules.statsd.Statsd;

class CountedInterceptor extends AbstractInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		final String statName = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
		final String combined = getCombinedPrefix() + ".count";
		Statsd.increment(statName);
		Statsd.increment(combined);
	
		Object ret = null;
		try {
			ret = invocation.proceed();
		} catch (Exception e) {
			Statsd.increment(combined + ".error");
			throw e;
		}
		return ret;
	}
}