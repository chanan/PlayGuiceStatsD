package playGuiceStatsD;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

class Matchers {
	
	private Matchers() { }

	private static final Matcher<Object> NONACTORS = new NonActors();
	private static final Matcher<Object> PUBLIC = new PublicMethods();
	
	public static Matcher<Object> nonActors() {
		return NONACTORS;
	}
	
	public static Matcher<Object> publicMethods() {
		return PUBLIC;
	}
	
	private static class NonActors extends AbstractMatcher<Object> implements Serializable {
	    public boolean matches(Object o) {
	    	if(o instanceof Class) {
				Class<?> clazz = (Class<?>) o;
				if(clazz.getSuperclass().getSimpleName().equalsIgnoreCase("UntypedActor")) return false;
				if(clazz.getSuperclass().getSimpleName().equalsIgnoreCase("HealthCheck")) return false;
				return true;
			}
			return false;
	    }

	    @Override public String toString() {
	      return "nonActors()";
	    }
	    
	    private static final long serialVersionUID = 0;
	}
	
	private static class PublicMethods extends AbstractMatcher<Object> implements Serializable {
	    public boolean matches(Object o) {
	    	if(o instanceof Method) {
				Method method = (Method) o;
				return Modifier.isPublic(method.getModifiers()) && !method.getDeclaringClass().getSimpleName().equalsIgnoreCase("Object");
			}
			return false;
	    }

	    @Override public String toString() {
	      return "publicMethods()";
	    }
	    
	    private static final long serialVersionUID = 0;
	}
}