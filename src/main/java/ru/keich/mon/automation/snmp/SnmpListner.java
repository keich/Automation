package ru.keich.mon.automation.snmp;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeListener;

/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
