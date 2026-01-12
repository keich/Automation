package ru.keich.mon.automation.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;

import lombok.extern.java.Log;
import ru.keich.mon.automation.dbdatasource.DBDataSourceService;
import ru.keich.mon.automation.httpdatasource.HttpDataSourceService;
import ru.keich.mon.automation.scripting.DBManager;
import ru.keich.mon.automation.scripting.HttpManager;
import ru.keich.mon.automation.scripting.InetAddressManager;
import ru.keich.mon.automation.scripting.LogManager;
import ru.keich.mon.automation.scripting.LogManager.Line;
import ru.keich.mon.automation.scripting.ScriptManager;
import ru.keich.mon.automation.scripting.SnmpManager;
import ru.keich.mon.automation.snmp.SnmpService;

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

@Log
public class ScriptContext {

	public static final String LANG_JS = "js";
	
	public static final String LOG_MSG_HIERAR_CIRCLE = "Circle found";

	public static final String MEMBER_LOG_NAME = "log";
	public static final String MEMBER_DB_NAME = "db";
	public static final String MEMBER_SCRIPT_NAME = "script";
	public static final String MEMBER_SNMP_NAME = "snmp";
	public static final String MEMBER_HTTPREQUEST_NAME = "httpRequest";
	public static final String MEMBER_DNS_NAME = "dns";

	private final DBDataSourceService dataSourceService;
	private final ScriptService scriptService;
	private final SnmpService snmpService;
	private final LogManager logm = new LogManager();
	private final HttpDataSourceService httpDataSourceService;
	private final String languare = LANG_JS;
	private final Context context;
	
	// TODO Use LinkedHashSet if java 21
	private final Stack<String> stack = new Stack<>();

	public ScriptContext(DBDataSourceService dataSourceService, ScriptService scriptService, SnmpService snmpService, HttpDataSourceService httpDataSourceService) {
		this.dataSourceService = dataSourceService;
		this.scriptService = scriptService;
		this.snmpService = snmpService;
		this.httpDataSourceService = httpDataSourceService;
		context = cretaeContext();
	}
	
	public void setLogCallBack(Consumer<Line> callBack) {
		logm.setCallBack(callBack);
	}

	private Context cretaeContext() {
		var context = Context.newBuilder(languare).allowHostAccess(HostAccess.ALL)
				.allowHostClassLookup(className -> true).build();
		var bindings = context.getBindings(languare);
		getMembers().entrySet().forEach(e -> {
			bindings.putMember(e.getKey(), e.getValue());
		});
		return context;
	}

	private Map<String, Object> getMembers() {
		var ret = new HashMap<String, Object>();
		ret.put(MEMBER_LOG_NAME, logm);
		ret.put(MEMBER_DB_NAME, new DBManager(dataSourceService));
		ret.put(MEMBER_SCRIPT_NAME, new ScriptManager(this));
		ret.put(MEMBER_SNMP_NAME, new SnmpManager(snmpService, this));
		ret.put(MEMBER_HTTPREQUEST_NAME, new HttpManager(httpDataSourceService, this));
		ret.put(MEMBER_DNS_NAME, new InetAddressManager());
		return ret;
	}
	
	public Map<String, Object> run(Script script, Object param) {
		if(stack.contains(script.getName())) {
			logm.severe(script.getName() + LOG_MSG_HIERAR_CIRCLE);
			return ScriptResult.err(LOG_MSG_HIERAR_CIRCLE);
		}
		Map<String, Object> result;
		stack.add(script.getName());
		try {
			var source = Source.newBuilder(LANG_JS, script.getCode(), script.getName()).build();
			var func = context.eval(source);
			result = ScriptResult.ok(func.execute(param));
		} catch (PolyglotException e) {
			var str = new StringBuffer(e.getMessage());
			var first = true;
			for (var tr : e.getPolyglotStackTrace()) {
				if (!tr.isGuestFrame()) {
					break;
				}
				if(first) {
					str.append("\nStackTrace: \n");
					first = false;
				} else {
					str.append('\n');
				}
				str.append(tr);
			}
			result = ScriptResult.err(str.toString());
		} catch (Exception e) {
			e.printStackTrace();
			result = ScriptResult.err(e.getMessage());
		}
		stack.pop();
		return result;
	}
	
	public String getScriptName() {
		return stack.peek();
	}
	
	public Map<String, Map<String, Object>> runChild(Object param) {
		var result = new HashMap<String, Map<String, Object>>();
		var scripts = scriptService.getChild(getScriptName());
		scripts.stream().forEach(script -> {
			result.put(script.getName(), run(script, param));
		});
		return result;
	}
	
	public void close() {
		context.close();
	}

}
