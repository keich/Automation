package ru.keich.mon.automation.scripting;

import java.util.HashMap;
import java.util.Map;

import org.graalvm.polyglot.Value;

import ru.keich.mon.automation.snmp.SnmpResult;
import ru.keich.mon.automation.snmp.SnmpService;
import ru.keich.mon.automation.snmp.SnmpTarget;

public class SnmpManager {

	public final String PARAM_ADDRESS = "address";
	public final String PARAM_COMMUNITY = "community";
	public final String PARAM_VERSION = "version";
	public final String PARAM_TIMEOUT = "timeout";
	public final String PARAM_OID = "oid";
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

	private <T> T castParam(String name, Map<String, Object> obj, Class<T> classz) {
		var param = obj.get(name);
		if (param == null) {
			throw new SnmpParamException("Parameter " + name + " not found");
		}

		if (classz.isInstance(param)) {
			return classz.cast(param);
		}
		throw new SnmpParamClassException(name + " cannot be cast into " + classz.getName());
	}
	
	private SnmpTarget mapToParams(Map.Entry<String, Map<String, Object>> e) {
		var key = e.getKey();
		var params = e.getValue();
		String address = castParam(PARAM_ADDRESS, params, String.class);
		String community = castParam(PARAM_COMMUNITY, params, String.class);
		int version = castParam(PARAM_VERSION, params, Integer.class);
		long timeout = castParam(PARAM_TIMEOUT, params, Integer.class);
		String oid = castParam(PARAM_OID, params, String.class);
		return new SnmpTarget(key, address, community, version, timeout, oid);
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
