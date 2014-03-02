package playGuiceStatsD;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import play.Configuration;
import play.Play;

public class Statsd {
	private static Configuration config;
	private final static String defaultValue = "guice.combined";
	private final static String configKey = "statsd.guice.combined.prefix";
	private static String combined = null;
	private static String address = null;
	private static boolean useServerAddress = true;
	
	private Statsd() { }
	
	private static String getCombinedPrefix() {
		if(combined != null) return combined;
		if(getConfig().getString(configKey) != null) {
			combined = config.getString(configKey);
		} else {
			combined = defaultValue;
		}
		return combined;
	}
	
	private static String getAddress() {
		if(address != null) return address;
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()){
			    NetworkInterface current = interfaces.nextElement();
			    if (!current.isUp() || current.isLoopback() || current.isVirtual()) continue;
			    Enumeration<InetAddress> addresses = current.getInetAddresses();
			    while (addresses.hasMoreElements()){
			        InetAddress current_addr = addresses.nextElement();
			        if (current_addr.isLoopbackAddress()) continue;
			        if (current_addr instanceof Inet4Address){
			        	address = current_addr.getHostAddress().replace('.', '_');
			        }
			    }
			}
		} catch(Exception e) {
			useServerAddress = false;
		}
		return address;
	}
	
	private static boolean isEnabled() {
		return getConfig().getBoolean("statsd.enabled");
	}
	
	private static Configuration getConfig() {
		if(config == null) config = Play.application().configuration();
		return config;
	}
	
	public static void increment(String statname) {
		if(!isEnabled()) return;
		play.modules.statsd.Statsd.increment(statname);
		play.modules.statsd.Statsd.increment(getCombinedPrefix());
		if(useServerAddress) {
			String address = getAddress();
			if(address != null) {
				play.modules.statsd.Statsd.increment(address + "." + statname);
				play.modules.statsd.Statsd.increment(address + "." + getCombinedPrefix());
			}
		}
	}
	
	public static void timing(String statname, long time) {
		if(!isEnabled()) return;
		play.modules.statsd.Statsd.timing(statname, time);
		play.modules.statsd.Statsd.timing(getCombinedPrefix(), time);
		if(useServerAddress) {
			String address = getAddress();
			if(address != null) {
				play.modules.statsd.Statsd.timing(address + "." + statname, time);
				play.modules.statsd.Statsd.timing(address + "." + getCombinedPrefix(), time);
			}
		}
	}
}