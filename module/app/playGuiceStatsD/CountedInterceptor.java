package playGuiceStatsD;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import play.modules.statsd.Statsd;

class CountedInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		final String statName = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
		Statsd.increment(statName);
		return invocation.proceed();
	}
}