package ru.keich.mon.automation.script.ui;

import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import ru.keich.mon.automation.script.Script;
import ru.keich.mon.automation.script.ScriptService;

public class ScriptHierarchicalDataProvider extends AbstractBackEndHierarchicalDataProvider<Script, Void> {

	private static final long serialVersionUID = -890378091045461883L;

	private final ScriptService scriptService;

	public ScriptHierarchicalDataProvider(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	@Override
	public int getChildCount(HierarchicalQuery<Script, Void> query) {
		return scriptService.getCountOrRoot(query);
	}

	@Override
	public boolean hasChildren(Script item) {
		return scriptService.getChildCountOrRoot(Optional.ofNullable(item.getName())) > 0;
	}

	@Override
	protected Stream<Script> fetchChildrenFromBackEnd(HierarchicalQuery<Script, Void> query) {
		return scriptService.getAllOrRoot(query);
	}

}
