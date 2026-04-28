package ru.keich.mon.automation.script;

import org.graalvm.polyglot.Value;

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


public class ScriptResult {

	public static final String KEY_ERR = "error";
	public static final String KEY_RESULT = "value";
	
	private String error;
	private Value value;

	private ScriptResult(Value value, String error) {
		this.value = value;
		this.error = error;
	}

	public static ScriptResult ok(Value value) {
		return new ScriptResult(value, null);
	}

	public static ScriptResult err(String errMsg) {
		return new ScriptResult(null, errMsg);
	}

	public boolean isError() {
		return error != null;
	}

	public Value getValue() {
		return value;
	}

	public String getError() {
		return error;
	}

	@Override
	public String toString() {
		if(error != null) {
			return error;
		}
		return value.toString();
	}
 
}
