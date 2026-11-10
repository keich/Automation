package ru.keich.mon.automation.script.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;

import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.script.Script;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.script.version.ScriptVersionService;
import ru.keich.mon.automation.script.version.dataProviderDto.ScriptVersionSave;
import ru.keich.mon.automation.scripting.ScriptCallBack;

public class ScriptsEdit extends Div {

	private static final long serialVersionUID = 5065116144261992678L;

	private static final double SPLIT_POS = 30;

	private final ScriptsEditLeft left;
	private final ScriptsEditRight right;
	private final ScriptService scriptService;
	private final ScheduleService scheduleService;
	private final ScriptVersionService scriptVersionService;

	public ScriptsEdit(ScriptService scriptService, ScheduleService scheduleService,
			ScriptVersionService scriptVersionService) {
		super();

		this.scheduleService = scheduleService;
		this.scriptService = scriptService;
		this.scriptVersionService = scriptVersionService;

		this.setSizeFull();
		this.setHeightFull();

		var dataHierarchicaProvider = new ScriptHierarchicalDataProvider(scriptService);
		var dataProvider = new ScriptNameDataProvider(scriptService);

		right = new ScriptsEditRight(dataProvider, this::save, this::delete, this::run);
		left = new ScriptsEditLeft(dataHierarchicaProvider, right::setScript, right::addNew, scriptService);
		var split = new SplitLayout(left, right);
		split.setSplitterPosition(SPLIT_POS);
		split.setSizeFull();
		split.setHeightFull();
		this.add(split);
	}

	private void save(ScriptVersionSave svs) {
		scriptService.save(svs.script());
		scriptVersionService.save(svs);
	}

	private void run(Script script, ScriptCallBack callBack) {
		scheduleService.execute(script, null, callBack);
	}

	private Boolean delete(Script script) {
		scriptService.delete(script);
		right.addNew();
		return false;
	}

}
