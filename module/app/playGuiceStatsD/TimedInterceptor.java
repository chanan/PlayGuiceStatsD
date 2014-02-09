package playGuiceStatsD;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import play.modules.statsd.Statsd;

public class TimedInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		final String statName = invocation.getMethod().getDeclaringClass().getName() + "." + invocation.getMethod().getName();
		final String combined = "classes.combined.time";
		final long start = System.currentTimeMillis();
		
		final Object ret = invocation.proceed();
		
		final long time = System.currentTimeMillis() - start;
		Statsd.timing(statName, time);
		Statsd.timing(combined, time);
		return ret;
	}
}