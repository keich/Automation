package ru.keich.mon.automation.script.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;

import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.script.Script;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.scripting.ScriptCallBack;

/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

	private void run(Script script, ScriptCallBack clackBack) {
		scheduleService.execute(script, null, clackBack);
	}

	private Boolean delete(Script script) {
		scriptService.delete(script);
		right.addNew();
		left.refresh();
		return false;
	}

}
