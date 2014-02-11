package playGuiceStatsD;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;

public class PublicMethodMatcher {
	private PublicMethodMatcher() {}
	
	public static Matcher<Object> publicMethods() {
		return PUBLIC;
	}
	
	private static final Matcher<Object> PUBLIC = new PublicMethods();
	
	private static class PublicMethods extends AbstractMatcher<Object> implements Serializable {
	    public boolean matches(Object o) {
	    	if(o instanceof Method) {
				Method method = (Method) o;
				return Modifier.isPublic(method.getModifiers());
			}
			return false;
	    }

	    @Override public String toString() {
	      return "publicMethods()";
	    }
	    
	    private static final long serialVersionUID = 0;
	}
}