package ru.keich.mon.automation.script;

import java.util.Collections;
import java.util.Map;

import org.graalvm.polyglot.Value;

public class ScriptResult {

	public static final String KEY_ERR = "error";
	public static final String KEY_RESULT = "value";

	public static Map<String, Object> ok(Value result) {
		return Collections.singletonMap(KEY_RESULT, result);

	}

	public static Map<String, Object> err(String errMsg) {
		return Collections.singletonMap(KEY_ERR, errMsg);
	}

}
