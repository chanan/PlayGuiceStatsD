PlayGuiceStatsD
===============

Overview
--------
A PlayFramework module that integrates StatsD with Guice. Although, this module does not require
that the StatsD PlayFramework filter be turned on, it is included in the module and it is 
recommanded that it is turned on.

The purpose of this module is to allow a developer to place counters and timers on services 
registered in Guice.

Installation
------------

Add the following to your build.sbt:

```java
resolvers += "release repository" at "http://chanan.github.io/maven-repo/releases/"
resolvers += "snapshot repository" at "http://chanan.github.io/maven-repo/snapshots/"
```

Add to your libraryDependencies:
```java
"playguicestatsd" %% "playguicestatsd" % "0.1.0"
```

Usage
-----

### Register in Global

Create a Guice Injector in Global using PlayGuiceStatsD (Note: AkkaGuice is not 
required for usage):

```java
public class Global extends GlobalSettings {
	private final Injector injector = Guice.createInjector(new PlayGuiceStatsDModule(), new GuiceModule());
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends EssentialFilter> Class<T>[] filters() {
		return new Class[] {play.modules.statsd.StatsdFilter.class};
	}

	@Override
	public <A> A getControllerInstance(Class<A> clazz) throws Exception {
		return injector.getInstance(clazz);
	}
}
```

### Annotations

Annotations can be placed at the class or method level:

```java
@Timed
@Counted
public class SayHelloImpl implements SayHello {
    ...
}
```

### Integrations with AkkaGuice

AkkaGuice can be used saftly with PlayGuiceStatsD:

```java
public class Global extends GlobalSettings {
	private final Injector injector = Guice.createInjector(new AkkaGuiceModule("services"), new PlayGuiceStatsDModule(), new GuiceModule());
	
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
	}
}
```

