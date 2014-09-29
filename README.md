PlayGuiceStatsD
===============

Overview
--------
A PlayFramework module that integrates StatsD with Guice. The purpose of this module is to allow a developer to place counters and timers on services 
registered in Guice. Also, the module provides a way to do health checks that integrate with StatsD.

Installation
------------

Add the following to your build.sbt:

```java
resolvers += "release repository" at "http://chanan.github.io/maven-repo/releases/"

resolvers += "snapshot repository" at "http://chanan.github.io/maven-repo/snapshots/"
```

Add to your libraryDependencies:

```java
"playguicestatsd" %% "playguicestatsd" % "0.6.0"
```

Usage
-----

### Register in Global

Create a Guice Injector in Global using PlayGuiceStatsD (Note: AkkaGuice is not 
required for usage):

```java
public class Global extends GlobalSettings {
	private final Injector injector = Guice.createInjector(new PlayGuiceStatsDModule("my.healthcheck.package"), new GuiceModule());
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends EssentialFilter> Class<T>[] filters() {
		return new Class[] {play.modules.statsd.StatsdFilter.class};
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) throws Exception {
		return injector.getInstance(clazz);
	}
	
	@Override
	public void onStart(Application arg0) {
		HealthChecker.Start(injector); //Only needed for healthchecks
	}	
}
```

If using healthchecks, you must pass the packages that contain the healthchecks to the Module.

All public methods for services registered in Guice will be counted and timed.

### Healthchecks

As seen in the three samples in the healthchecks package, extend playGuiceStatsD.healthChecks.HealthCheck to create a healthcheck. The check should return a Result.healthy() if the check passed
otherwise return a Result.unhealthy() or throw an exception. Health checks are done in the background in a separate actor. The result of a check is logged at info level if the check is successful
and to the Error log if not. The time and count of the checks are also sent to StatsD.

### Configuration for HealthChecks

StatsD must be enabled via the "statsd.enabled" setting. "statsd.healthchecks.enabled" must also be set to true. This will cause HealthChecks to be done every 5 minutes. If you wish to use
another schedule you can also supply:

* statsd.healthchecks.initialDelay = 1 minute
* statsd.healthchecks.interval = 5 minutes

### HealthCheck Timeouts

You can choose to consider a healthcheck that takes too long as failed. If you don't supply "statsd.healthchecks.timeout" in the application.conf then this feature will be turned off. Otherwise
a healthcheck that takes longer than the supplied value will be considered as failed. You can see this in the sample app in the HealthCheckRandomTime check that sometimes takes 500ms and sometimes
takes 10,000ms. 

### HealthCheck Webpage

playGuiceStatsD comes with a page that displays the healthchecks. To use it add the following line to your routes file:

```
->     /         					playGuiceStatsD.Routes
```

This will enable the healthcheck page on the /healthcheck route.

Integrations with AkkaGuice
--------------------

AkkaGuice can be used safely with PlayGuiceStatsD:

```java
public class Global extends GlobalSettings {
	private final Injector injector = Guice.createInjector(new AkkaGuiceModule("services"), new PlayGuiceStatsDModule("healthchecks"), new GuiceModule());
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends EssentialFilter> Class<T>[] filters() {
		return new Class[] {play.modules.statsd.StatsdFilter.class};
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) throws Exception {
		return injector.getInstance(clazz);
	}
	
	@Override
	public void onStart(Application arg0) {
		AkkaGuice.InitializeInjector(injector, "services");
		HealthChecker.Start(injector);
	}
}
```

History
-------

* 0.6.0 - Support for Play 2.3
* 0.5.1 - Renamed global in module
* 0.5.0 - Added a healthcheck page and timeouts for healthchecks
* 0.4.0 - Added grouping by server ip

