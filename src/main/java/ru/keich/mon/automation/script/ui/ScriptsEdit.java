package ru.keich.mon.automation.script.ui;

import java.time.Instant;
import java.util.Optional;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;

import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.script.Script;
import ru.keich.mon.automation.script.ScriptService;
import ru.keich.mon.automation.script.version.ScriptVersionService;
import ru.keich.mon.automation.script.version.ScriptEditRightVersion;
import ru.keich.mon.automation.script.version.ScriptVersion;
import ru.keich.mon.automation.script.version.dataProviderDto.ScriptVersionSave;
import ru.keich.mon.automation.scripting.ScriptCallBack;

/*
 * Copyright 2026 the original author or authors.
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

public class ScriptsEdit extends Div implements BeforeEnterObserver {

	private static final long serialVersionUID = 5065116144261992678L;

	private static final double SPLIT_POS = 30;

	private final ScriptsEditLeft left;
	private final ScriptsEditRight right;
	private final SplitLayout split;
	private final ScriptService scriptService;
	private final ScheduleService scheduleService;
	private final ScriptVersionService scriptVersionService;
	private final ScriptVersionDataProvider dataProviderVersion;
	private final ScriptEditRightVersion scriptEditRightVersion;

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
		dataProviderVersion = new ScriptVersionDataProvider(scriptVersionService);

		right = new ScriptsEditRight(dataProvider, this::save, this::delete, this::run, dataProviderVersion,
				this::getScriptVersionByInstant, this::setRightContentOnVersions);
		left = new ScriptsEditLeft(dataHierarchicaProvider, this::setScript, right::addNew, scriptService);
		split = new SplitLayout(left, right);
		split.setSplitterPosition(SPLIT_POS);
		split.setSizeFull();
		split.setHeightFull();
		this.add(split);

		scriptEditRightVersion = new ScriptEditRightVersion(scriptVersionService, dataProvider,
				this::getScriptVersionByInstant);
	}

	private void _removeLastElement() {
		this.getComponentAt(this.getComponentCount() - 1).removeFromParent();
	}

	private boolean setRightContentOnVersions(String scriptName) {
		_removeLastElement();
		scriptEditRightVersion.setScriptName(scriptName);
		add(scriptEditRightVersion);
		return true;
	}

	void setScript(Script script) {
		dataProviderVersion.setScriptName(script.getName());
		right.setScript(script);
	}

	private Optional<ScriptVersion> getScriptVersionByInstant(Instant id) {
		return scriptVersionService.findById(id);
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

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		_removeLastElement();
		this.add(split);
	}

}
