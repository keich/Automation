package ru.keich.mon.automation.ui.ScriptsUI;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import ru.keich.mon.automation.script.ScriptService;

public class ScriptDataProvider extends AbstractBackEndDataProvider<String, String> {

	private static final long serialVersionUID = 2728430003441011070L;

	private final ScriptService scriptService;

	public ScriptDataProvider(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	@Override
	protected Stream<String> fetchFromBackEnd(Query<String, String> query) {
		return scriptService.getAll(query);
	}

	@Override
	protected int sizeInBackEnd(Query<String, String> query) {
		return scriptService.getCount(query);
	}

}
