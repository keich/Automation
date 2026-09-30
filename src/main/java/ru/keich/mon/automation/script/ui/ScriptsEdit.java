package ru.keich.mon.automation.script.ui;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.html.Div;
import ru.keich.mon.automation.scripting.LogManager;
import com.vaadin.flow.component.splitlayout.SplitLayout;

import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.script.Script;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.scripting.LogManager;
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
		var dataProvider = new ScriptNameDataProvider(scriptService);

		right = new ScriptsEditRight(dataProvider, this::save, this::delete, this::run);
		left = new ScriptsEditLeft(dataHierarchicaProvider, right::setScript, right::addNew,scriptService);

		var split = new SplitLayout(left, right);
		split.setSplitterPosition(SPLIT_POS);
		split.setSizeFull();
		split.setHeightFull();
		this.add(split);
		
	}
	@Override
	protected void onAttach(AttachEvent attachEvent) {
		super.onAttach(attachEvent);
		
		attachEvent.getUI().setPollInterval(1000);
		attachEvent.getUI().addPollListener(event -> left.refresh());
	}
	
	

	private void save(Script script) {
		scriptService.save(script);
		left.refresh();
	}

	private void run(Script script, ScriptCallBack callBack) {
		ScriptCallBack refreshCallBack = new ScriptCallBack() {
			
			@Override
			public void onLog(LogManager.Line line) {
				callBack.onLog(line);
			}
			
			@Override
			public void onResult(String data) {
				callBack.onResult(data);
				left.refresh();
			}
			
			@Override 
			public void onError(Exception e) {
				callBack.onError(e);
				left.refresh();
			}
		};
		scheduleService.execute(script, null, refreshCallBack);
		left.refresh();
	}

	private Boolean delete(Script script) {
		scriptService.delete(script);
		right.addNew();
		left.refresh();
		return false;
	}

}
