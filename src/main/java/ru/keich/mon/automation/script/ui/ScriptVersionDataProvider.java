package ru.keich.mon.automation.script.ui;

import java.util.Map;
import java.util.stream.Stream;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import ru.keich.mon.automation.script.version.ScriptVersion;
import ru.keich.mon.automation.script.version.ScriptVersionService;
import lombok.RequiredArgsConstructor;
import java.time.Instant;

@RequiredArgsConstructor
public class ScriptVersionDataProvider extends AbstractBackEndDataProvider<Instant, String> {
	private static final long serialVersionUID = -7807713895993224008L;
	private final ScriptVersionService service;
	
	@Override
	protected Stream<Instant> fetchFromBackEnd(Query<Instant,  String> query) {
		return service.findAllByName(query.getPageSize(),query.getPage(),query.getFilter().orElse(""));
		
	}
	@Override
	protected int sizeInBackEnd(Query<Instant,  String> query) {
		return service.getAllByNameCount(query.getFilter().orElse(""));
	}
}
