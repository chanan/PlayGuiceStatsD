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
        private static final int PRIME = 31;

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

        @Override
        public boolean equals(Object o) {
            if (this == o) { return true; }
            if (o == null || getClass() != o.getClass()) { return false; }
            final Result result = (Result) o;
            return healthy == result.healthy &&
                    !(error != null ? !error.equals(result.error) : result.error != null) &&
                    !(message != null ? !message.equals(result.message) : result.message != null);
        }

        @Override
        public int hashCode() {
            int result = (healthy ? 1 : 0);
            result = PRIME * result + (message != null ? message.hashCode() : 0);
            result = PRIME * result + (error != null ? error.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            final StringBuilder builder = new StringBuilder("HealthCheck Result Result: {");
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
    	final String name = getClassName(this.getClass().getName());
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
