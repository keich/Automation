package ru.keich.mon.automation.scripting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.keich.mon.automation.httpdatasource.HttpDataSourceService;
import ru.keich.mon.automation.httpdatasource.HttpResult;

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

public class HttpManager {

	public final String RESULT_PARAM_STATUS = "status";
	public final String RESULT_PARAM_ERR = "errorMessage";
	public final String RESULT_PARAM_DATA = "data";
	
	private final HttpDataSourceService httpDataSourceService;

	public HttpManager(HttpDataSourceService httpDataSourceService, Object lock) {
		this.httpDataSourceService = httpDataSourceService;
	}

	private Map<String, Object> resultToMap(HttpResult result) {
		var ret = new HashMap<String, Object>();
		ret.put(RESULT_PARAM_STATUS, result.getStatus());
		ret.put(RESULT_PARAM_ERR, result.getErrMessage());
		ret.put(RESULT_PARAM_DATA, result.getData());
		return ret;
	}
	
	public Map<String, Object> get(String name, String path, Map<String, List<String>> params, Map<String, List<String>> headers) {
		return resultToMap(httpDataSourceService.getRequest(name, path, params, headers));
	}
	
	public Map<String, Object> delete(String name, String path, Map<String, List<String>> params, Map<String, List<String>> headers, String data) {
		return resultToMap(httpDataSourceService.delRequest(name, path, params, headers, data));
	}
	
	public Map<String, Object> post(String name, String path, Map<String, List<String>> params, Map<String, List<String>> headers, String data) {
		return resultToMap(httpDataSourceService.postRequest(name, path, params, headers, data));
	}

}
