package ru.keich.mon.automation.scripting;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressManager {
	
	public String getHostName(String ip) {
		var result = "";
		try {
			result = InetAddress.getByName(ip).getHostName().toLowerCase();
		} catch (UnknownHostException e) {
			// nothing
		}
		return result;
	}
	
}
