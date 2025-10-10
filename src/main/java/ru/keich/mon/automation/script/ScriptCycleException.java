package ru.keich.mon.automation.script;

public class ScriptCycleException extends RuntimeException {

	private static final long serialVersionUID = 9020611535250744463L;

	public ScriptCycleException(String message) {
		super(message);
	}

}
