package ru.keich.mon.automation.scripting;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.polyglot.Value;
import org.snmp4j.smi.OID;

import ru.keich.mon.automation.snmp.SnmpResult;
import ru.keich.mon.automation.snmp.SnmpService;
import ru.keich.mon.automation.snmp.SnmpTarget;

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

public class SnmpManager {

	public final String PARAM_ADDRESS = "address";
	public final String PARAM_COMMUNITY = "community";
	public final String PARAM_VERSION = "version";
	public final String PARAM_TIMEOUT = "timeout";
	public final String PARAM_OID = "oids";
	public final String PARAM_CALLBACK = "callback";
	
	public final String RESULT_PARAM_KEY = "key";
	public final String RESULT_PARAM_ERR = "errorMessage";
	public final String RESULT_PARAM_DATA = "data";


	private final SnmpService snmpService;
	
	private final Object lock;

	public SnmpManager(SnmpService snmpService, Object lock) {
		this.snmpService = snmpService;
		this.lock = lock;
	}

	public class SnmpParamException extends RuntimeException {

		private static final long serialVersionUID = 7799579428442102933L;

		public SnmpParamException(String message) {
			super(message);
		}

	}

	public class SnmpParamClassException extends RuntimeException {

		private static final long serialVersionUID = 7799579428442102933L;

		public SnmpParamClassException(String message) {
			super(message);
		}

	}

	private <T> T castParam(String name, Object param, Class<? extends T> classz) {
		if (classz.isInstance(param)) {
			return classz.cast(param);
		}
		throw new SnmpParamClassException(name + " cannot be cast into " + classz.getName());
	}

	private <T> T getParam(String name, Map<String, T> obj) {
		var param = obj.get(name);
		if (param == null) {
			throw new SnmpParamException("Parameter " + name + " not found");
		}
		return param;
	}
	
	private SnmpTarget mapToParams(Map.Entry<String, Map<String, Object>> e) {
		var key = e.getKey();
		var params = e.getValue();

		String address = castParam(PARAM_ADDRESS, getParam(PARAM_ADDRESS, params), String.class);
		String community = castParam(PARAM_COMMUNITY, getParam(PARAM_COMMUNITY, params), String.class);
		int version = castParam(PARAM_VERSION, getParam(PARAM_VERSION, params), Integer.class);
		long timeout = castParam(PARAM_TIMEOUT, getParam(PARAM_TIMEOUT, params), Integer.class);

		Collection<?> coll = castParam(PARAM_OID, getParam(PARAM_OID, params), Collection.class);

		var oids = coll.stream()
				.map(oid -> castParam(PARAM_OID, oid, String.class))
				.map(OID::new)
				.toArray(OID[]::new);

		return new SnmpTarget(key, address, community, version, timeout, oids);
	}
	
	private Map<String, Object> resultToMap(SnmpResult snmpResult) {
		var ret = new HashMap<String, Object>();
		ret.put(RESULT_PARAM_KEY, snmpResult.getKey());
		ret.put(RESULT_PARAM_ERR, snmpResult.getErrMessage());
		ret.put(RESULT_PARAM_DATA, snmpResult.getData());
		return ret;
	}

	public void walk(Map<String, Map<String, Object>> params, Value callback) {
		if (!callback.canExecute()) {
			throw new SnmpParamException("Cannot execute callback function");
		}
		var targets = params.entrySet().stream().map(this::mapToParams).toList();
		snmpService.walk(targets, r -> {
			synchronized(lock) {
				callback.execute(resultToMap(r));
			}
		});
	}

}
