package playGuiceStatsD.healthChecks;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import akka.actor.ActorRef;

class HealthCheckSession {
	private final ActorRef sender;
	private final UUID key;
	private final Map<Class<? extends HealthCheck>, HealthCheckHolder> holders = new HashMap<Class<? extends HealthCheck>, HealthCheckHolder>();
	
	public HealthCheckSession(UUID key) {
		this.key = key;
		this.sender = null;
	}
	
	public HealthCheckSession(UUID key, ActorRef sender) {
		this.key = key;
		this.sender = sender;
	}
	
	public void addHolder(Class<? extends HealthCheck> key, HealthCheckHolder healthCheckHolder) {
		holders.put(key, healthCheckHolder);
	}
	
	public HealthCheckHolder getHolder(Class<? extends HealthCheck> key) {
		return holders.get(key);
	}

	public ActorRef getSender() {
		return sender;
	}

	public Collection<HealthCheckHolder> getHolders() {
		return holders.values();
	}

	public UUID getKey() {
		return key;
	}

	@Override
	public String toString() {
		return "HealthCheckSession {key=" + key + ", sender=" + sender + ", holders=[" + holders + "]}";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((holders == null) ? 0 : holders.hashCode());
		result = prime * result + ((sender == null) ? 0 : sender.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		HealthCheckSession other = (HealthCheckSession) obj;
		if (key == null) {
			if (other.key != null) return false;
		} else if (!key.equals(other.key)) return false;
		if (holders == null) {
			if (other.holders != null) return false;
		} else if (!holders.equals(other.holders)) return false;
		if (sender == null) {
			if (other.sender != null) return false;
		} else if (!sender.equals(other.sender)) return false;
		return true;
	}
}