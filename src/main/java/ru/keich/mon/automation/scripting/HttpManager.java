package ru.keich.mon.automation.scripting;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graalvm.polyglot.Value;

import ru.keich.mon.automation.httpdatasource.HttpDataSourceService;
import ru.keich.mon.automation.httpdatasource.HttpResult;

public class HttpManager {

	public final String RESULT_PARAM_STATUS = "status";
	public final String RESULT_PARAM_ERR = "errorMessage";
	public final String RESULT_PARAM_DATA = "data";
	
	private final HttpDataSourceService httpDataSourceService;

	public HttpManager(HttpDataSourceService httpDataSourceService) {
		this.httpDataSourceService = httpDataSourceService;
	}

	private Map<String, Object> resultToMap(HttpResult result) {
		var ret = new HashMap<String, Object>();
		ret.put(RESULT_PARAM_STATUS, result.getStatus());
		ret.put(RESULT_PARAM_ERR, result.getErrMessage());
		ret.put(RESULT_PARAM_DATA, result.getData());
		return ret;
	}
	
	public void get(String name, String path, Map<String, List<String>> params, Map<String, String> headers, Value callback) {
		httpDataSourceService.getRequest(name, path, params, headers, result -> callback.execute(resultToMap(result)));
	}

}
