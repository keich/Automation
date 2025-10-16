package ru.keich.mon.automation.scripting;

import java.util.Map;

import ru.keich.mon.automation.script.ScriptContext;

public class ScriptManager {

	private final ScriptContext scriptContext;

	public ScriptManager(ScriptContext scriptContext) {
		super();
		this.scriptContext = scriptContext;
	}
	
	public Map<String,Map<String,Object>> runChild(Object param) {
		return scriptContext.runChild(param);
	}
	
}
