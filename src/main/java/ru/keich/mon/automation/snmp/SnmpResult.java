package ru.keich.mon.automation.snmp;

import java.util.HashMap;
import java.util.Map;

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
