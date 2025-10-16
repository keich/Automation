package ru.keich.mon.automation.script;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Stack;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;

import ru.keich.mon.automation.dbdatasource.DBDataSourceService;
import ru.keich.mon.automation.scripting.DBManager;
import ru.keich.mon.automation.scripting.LogManager;
import ru.keich.mon.automation.scripting.ScriptManager;

public class ScriptContext {

	public static final String LANG_JS = "js";
	
	public static final String LOG_MSG_HIERAR_CIRCLE = "Circle found";

	public static final String MEMBER_LOG_NAME = "log";
	public static final String MEMBER_DB_NAME = "db";
	public static final String MEMBER_SCRIPT_NAME = "script";
	
	public static final String LOG_MSG_RUN_OK = ": running with result: ";
	public static final String LOG_MSG_RUN_ERR = ": running with error: ";

	private final DBDataSourceService dataSourceService;
	private final ScriptService scriptService;
	private final LogManager logm;
	private final String languare = LANG_JS;
	private final Context context;
	
	// TODO Use LinkedHashSet if java 21
	private final Stack<String> stack = new Stack<>();

	public ScriptContext(LogManager logm, DBDataSourceService dataSourceService, ScriptService scriptService) {
		super();
		this.dataSourceService = dataSourceService;
		this.scriptService = scriptService;
		this.logm = logm;
		context = cretaeContext();
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
		return ret;
	}
	
	public Map<String, Object> run(Script script, Object param) {
		if(stack.contains(script.getName())) {
			logm.severe(script.getName() + LOG_MSG_HIERAR_CIRCLE);
			return ScriptResult.err(LOG_MSG_HIERAR_CIRCLE);
		}
		Map<String, Object>  result;
		stack.add(script.getName());
		try {
			var func = context.eval(LANG_JS, script.getCode());
			result = ScriptResult.ok(func.execute(param));
		} catch (Exception e) {
			result = ScriptResult.err(e.getMessage());
		}
		stack.pop();
		if (result.get(ScriptResult.KEY_RESULT) != null) {
			logm.info(script.getName() + LOG_MSG_RUN_OK + result.get(ScriptResult.KEY_RESULT));
		} else {
			logm.severe(script.getName() + LOG_MSG_RUN_ERR + result.get(ScriptResult.KEY_ERR));
		}
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

}
