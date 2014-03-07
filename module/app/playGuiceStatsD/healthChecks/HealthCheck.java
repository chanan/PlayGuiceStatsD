package playGuiceStatsD.healthChecks;

import playGuiceStatsD.Statsd;

/**
 * A health check for a component of your application.
 * 
 * Taken from Metrics project: https://github.com/dropwizard/metrics/blob/master/metrics-healthchecks/src/main/java/com/codahale/metrics/health/HealthCheck.java
 */
public abstract class HealthCheck {
    /**
     * The result of a {@link HealthCheck} being run. It can be healthy (with an optional message)
     * or unhealthy (with either an error message or a thrown exception).
     */
    public static class Result {
        private static final Result HEALTHY = new Result(true, null, null);

        /**
         * Returns a healthy {@link Result} with no additional message.
         *
         * @return a healthy {@link Result} with no additional message
         */
        public static Result healthy() {
            return HEALTHY;
        }

        /**
         * Returns a healthy {@link Result} with an additional message.
         *
         * @param message an informative message
         * @return a healthy {@link Result} with an additional message
         */
        public static Result healthy(String message) {
            return new Result(true, message, null);
        }

        /**
         * Returns a healthy {@link Result} with a formatted message.
         * <p/>
         * Message formatting follows the same rules as {@link String#format(String, Object...)}.
         *
         * @param message a message format
         * @param args    the arguments apply to the message format
         * @return a healthy {@link Result} with an additional message
         * @see String#format(String, Object...)
         */
        public static Result healthy(String message, Object... args) {
            return healthy(String.format(message, args));
        }

        /**
         * Returns an unhealthy {@link Result} with the given message.
         *
         * @param message an informative message describing how the health check failed
         * @return an unhealthy {@link Result} with the given message
         */
        public static Result unhealthy(String message) {
            return new Result(false, message, null);
        }

        /**
         * Returns an unhealthy {@link Result} with a formatted message.
         * <p/>
         * Message formatting follows the same rules as {@link String#format(String, Object...)}.
         *
         * @param message a message format
         * @param args    the arguments apply to the message format
         * @return an unhealthy {@link Result} with an additional message
         * @see String#format(String, Object...)
         */
        public static Result unhealthy(String message, Object... args) {
            return unhealthy(String.format(message, args));
        }

        /**
         * Returns an unhealthy {@link Result} with the given error.
         *
         * @param error an exception thrown during the health check
         * @return an unhealthy {@link Result} with the given error
         */
        public static Result unhealthy(Throwable error) {
            return new Result(false, error.getMessage(), error);
        }
        
        static Result setName(Result result, String name) {
			return new Result(result.healthy, result.getMessage(), result.getError(), name);
        }

        private final boolean healthy;
        private final String message;
        private final Throwable error;
        private final String name;
        
        private Result(boolean isHealthy, String message, Throwable error) {
            this.healthy = isHealthy;
            this.message = message;
            this.error = error;
            this.name = null;
        }
        
        private Result(boolean isHealthy, String message, Throwable error, String name) {
            this.healthy = isHealthy;
            this.message = message;
            this.error = error;
            this.name = name;
        } 
        
        /**
         * Returns {@code true} if the result indicates the component is healthy; {@code false}
         * otherwise.
         *
         * @return {@code true} if the result indicates the component is healthy
         */
        public boolean isHealthy() {
            return healthy;
        }

        /**
         * Returns any additional message for the result, or {@code null} if the result has no
         * message.
         *
         * @return any additional message for the result, or {@code null}
         */
        public String getMessage() {
            return message;
        }

        /**
         * Returns any exception for the result, or {@code null} if the result has no exception.
         *
         * @return any exception for the result, or {@code null}
         */
        public Throwable getError() {
            return error;
        }
        
        public String getName() {
        	return name;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("HealthCheck Result: {");
            if(name != null) {
            	builder.append("Name= ");
            	builder.append(name);
            	builder.append(", ");
            }
            builder.append("isHealthy=");
            builder.append(healthy);
            if (message != null) {
                builder.append(", message=").append(message);
            }
            if (error != null) {
                builder.append(", error=").append(error);
            }
            builder.append('}');
            return builder.toString();
        }

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((error == null) ? 0 : error.hashCode());
			result = prime * result + (healthy ? 1231 : 1237);
			result = prime * result + ((message == null) ? 0 : message.hashCode());
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			Result other = (Result) obj;
			if (error == null) {
				if (other.error != null) return false;
			} else if (!error.equals(other.error)) return false;
			if (healthy != other.healthy) return false;
			if (message == null) {
				if (other.message != null) return false;
			} else if (!message.equals(other.message))
				return false;
			if (name == null) {
				if (other.name != null) return false;
			} else if (!name.equals(other.name)) return false;
			return true;
		}
    }

    /**
     * Perform a check of the application component.
     *
     * @return if the component is healthy, a healthy {@link Result}; otherwise, an unhealthy {@link
     *         Result} with a descriptive error message or exception
     * @throws Exception if there is an unhandled error during the health check; this will result in
     *                   a failed health check
     */
    protected abstract Result check() throws Exception;

    /**
     * Executes the health check, catching and handling any exceptions raised by {@link #check()}.
     *
     * @return if the component is healthy, a healthy {@link Result}; otherwise, an unhealthy {@link
     *         Result} with a descriptive error message or exception
     */
    public Result execute() {
    	final String name = getClassName(this.getClass().getSimpleName());
    	final String statName = "healthchecks." + name;
    	Result check = null;
    	boolean error = false;
    	final long start = System.currentTimeMillis();
        try {
            check = check();
            error = check.isHealthy();
        } catch (Exception e) {
        	check = Result.unhealthy(e);
        	error = true;
        }
        final long time = System.currentTimeMillis() - start;
    	check = Result.setName(check, name);
    	Statsd.timing(statName, time);
    	Statsd.increment(statName);
		if(error) {
			Statsd.timing("healthchecks.errors", time);
			Statsd.increment("healthchecks.errors");
		}
        return check;
    }

    private String getClassName(String name) {
    	String output = name;
    	if(output.contains("$$")) {
    		output = output.substring(0, output.indexOf("$$"));
    	}
		return output;
    }
}
