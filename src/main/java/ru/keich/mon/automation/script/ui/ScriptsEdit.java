package ru.keich.mon.automation.script.ui;

import java.util.function.Consumer;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;

import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.script.Script;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.scripting.LogManager;

public class ScriptsEdit extends Div {

	private static final long serialVersionUID = 5065116144261992678L;

	private static final double SPLIT_POS = 20;

	private final ScriptsEditLeft left;
	private final ScriptsEditRight right;

	private final ScriptService scriptService;
	private final ScheduleService scheduleService;

	public ScriptsEdit(ScriptService scriptService, ScheduleService scheduleService) {
		super();

		this.scheduleService = scheduleService;
		this.scriptService = scriptService;

		this.setSizeFull();
		this.setHeightFull();

		var dataHierarchicaProvider = new ScriptHierarchicalDataProvider(scriptService);
		var dataProvider = new ScriptDataProvider(scriptService);

		right = new ScriptsEditRight(dataProvider, this::save, this::delete, this::run);
		left = new ScriptsEditLeft(dataHierarchicaProvider, right::setScript, right::addNew);

		var split = new SplitLayout(left, right);
		split.setSplitterPosition(SPLIT_POS);
		split.setSizeFull();
		split.setHeightFull();
		this.add(split);
	}

	private void save(Script script) {
		scriptService.save(script);
		left.refresh();
	}

	private void run(Script script, Consumer<LogManager.Line> clackBack) {
		scheduleService.execute(script, null, clackBack);
	}

	private Boolean delete(Script script) {
		scriptService.delete(script);
		right.addNew();
		left.refresh();
		return false;
	}

}
