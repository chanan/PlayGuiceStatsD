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
"playguicestatsd" %% "playguicestatsd" % "0.1.0-SNAPSHOT"
```

Usage
-----

### Register in Global

Create a Guice Injector in Global using PlayGuiceStatsD (Note: AkkaGuice is not 
required for usage):

```java
import play.Application;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import playGuiceStatsD.PlayGuiceStatsD;
import akkaGuice.AkkaGuice;

import com.google.inject.Injector;

public class Global extends GlobalSettings {
	private Injector injector = PlayGuiceStatsD.CreateInjector();
	
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
		injector = injector.createChildInjector(new GuiceModule());
		injector = AkkaGuice.Startup(injector, "services");
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

