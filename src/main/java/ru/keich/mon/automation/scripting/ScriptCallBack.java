package ru.keich.mon.automation.scripting;

public class ScriptCallBack {

	public final static ScriptCallBack EMPTY_CALLBACK = new ScriptCallBack();
	
	public void onResult(String data) {

	}

	public void onError(Exception e) {

	}

	public void onLog(LogManager.Line line) {

	}

}
