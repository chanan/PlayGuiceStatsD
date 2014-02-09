package playGuiceStatsD;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import play.modules.statsd.Statsd;

class CountedInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		final String statName = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
		final String combined = "classes.combined.time";
		Statsd.increment(statName);
		Statsd.increment(combined);
		return invocation.proceed();
	}
}