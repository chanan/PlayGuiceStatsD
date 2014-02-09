package playGuiceStatsD;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import play.modules.statsd.Statsd;

public class TimedInterceptor implements MethodInterceptor {
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		final long start = System.currentTimeMillis();
		
		final Object ret = invocation.proceed();
		
		final long time = System.currentTimeMillis() - start;
		Statsd.timing("MyStat", time);
		return ret;
	}
}