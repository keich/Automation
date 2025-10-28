package ru.keich.mon.automation.snmp;

import java.util.HashMap;
import java.util.Map;

public class SnmpResult {
	private Map<String, String> data = new HashMap<>();
	private String errMessage = "";
	private final String key;

	public SnmpResult(String key) {
		this.key = key;
	}
	
	public void put(String key, String value) {
		data.put(key, value);
	}
	
	public Map<String, String> getData() {
		return data;
	}

	public void setErrMessage(String message) {
		errMessage = message;
	}

	public String getErrMessage() {
		return errMessage;
	}
	
	public String getKey() {
		return key;
	}

}
