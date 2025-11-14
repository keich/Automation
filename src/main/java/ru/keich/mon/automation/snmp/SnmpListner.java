package ru.keich.mon.automation.snmp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeListener;

public class SnmpListner implements TreeListener {

	private final Map<String, SnmpResult> results = new HashMap<>();
	private final LinkedList<String> finished = new LinkedList<>();

	@Override
	public synchronized boolean next(TreeEvent event) {
		if (!event.isError()) {
			doEvent(event);
		} else {
			var key = event.getUserObject().toString();
			results.get(key).setErrMessage(event.getErrorMessage());
		}
		return true;
	}

	@Override
	public synchronized void finished(TreeEvent event) {
		var key = event.getUserObject().toString();
		if (!event.isError()) {
			doEvent(event);
		} else {
			results.get(key).setErrMessage(event.getErrorMessage());
		}
		finished.add(key);
		this.notifyAll();
	}

	@Override
	public synchronized boolean isFinished() {
		return results.size() == 0;
	}

	private void doEvent(TreeEvent event) {
		var bindings = event.getVariableBindings();
		var key = event.getUserObject().toString();
		var result = results.get(key);
		if (bindings != null) {
			for (var b : bindings) {
				result.put(b.getOid().toDottedString(), b.toValueString());
			}
		}
	}

	public synchronized void addForWaiting(String key) {
		results.put(key, new SnmpResult(key));
	}

	public synchronized boolean hasFinished() {
		return finished.size() > 0;
	}

	public synchronized SnmpResult getFinished() {
		if (results.size() > 0) {
			var key = finished.pollFirst();
			return results.remove(key);
		}
		return null;
	}

}
