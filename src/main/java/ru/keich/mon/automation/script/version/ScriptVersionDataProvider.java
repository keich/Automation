package ru.keich.mon.automation.script.version;

import java.util.Arrays;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import ru.keich.mon.automation.script.version.dataProviderDto.ScriptVersionQuery;

public class ScriptVersionDataProvider extends AbstractBackEndDataProvider<Integer, ScriptVersionQuery> {

	private final ScriptVersionService _service;
	
	public ScriptVersionDataProvider(ScriptVersionService service) {
		this._service = service;
	}
	
	@Override
	protected Stream<Integer> fetchFromBackEnd(Query<Integer, ScriptVersionQuery> q) {
		var r = this._service.findByNameOrderByVersion(q).get();
		return r.stream();
	}

	@Override
	protected int sizeInBackEnd(Query<Integer, ScriptVersionQuery> query) {
		return this._service.countByName(query);
	}

}
