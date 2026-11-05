package ru.keich.mon.automation.script.ui;

import java.time.Instant;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.keich.mon.automation.script.version.ScriptVersionService;

@RequiredArgsConstructor
@Setter
public class ScriptVersionDataProvider extends AbstractBackEndDataProvider<Instant, String> {
	private static final long serialVersionUID = -7807713895993224008L;
	private final ScriptVersionService service;
	private String scriptName = "";

	@Override
	protected Stream<Instant> fetchFromBackEnd(Query<Instant, String> query) {
		return service.findAllByName(query.getPageSize(), query.getPage(), scriptName);

	}

	@Override
	protected int sizeInBackEnd(Query<Instant, String> query) {
		return service.getAllByNameCount(scriptName);
	}

}
