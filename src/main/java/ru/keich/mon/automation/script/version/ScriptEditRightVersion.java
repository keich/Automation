package ru.keich.mon.automation.script.version;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import ru.keich.mon.automation.script.ui.ScriptNameDataProvider;

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

public class ScriptEditRightVersion extends Div {

	private static final long serialVersionUID = 9051167217283860982L;
	private String scriptName;
	private final ComboBox<String> versionField;
	private final Grid<ScriptVersion> grid;

	public void setScriptName(String name) {
		this.scriptName = name;
		versionField.setValue(name);
		grid.getDataProvider().refreshAll();
	}

	public ScriptEditRightVersion(ScriptVersionService versionsService, ScriptNameDataProvider nameProvider,
			Function<Instant, Optional<ScriptVersion>> getScriptVersionByInstant) {
		var header = new Header();
		versionField = new ComboBox<String>();
		versionField.setItemLabelGenerator(i -> i.toString());
		versionField.setItems(nameProvider);
		versionField.addValueChangeListener(i -> {
			setScriptName(i.getValue());
		});

		header.add(versionField);

		var layout = new VerticalLayout();

		grid = new Grid<ScriptVersion>();
		grid.addColumn(ScriptVersion::getId).setHeader("ID");
		grid.addColumn(ScriptVersion::getNote).setHeader("NOTE");
		grid.setItemsPageable(page -> {
			return versionsService.findAllByName(page.getPageSize(), page.getPageNumber(), scriptName).toList();
		});

		grid.addItemDoubleClickListener((evnt) -> {

		});
		layout.add(grid);
		this.add(header);
		this.add(layout);
	}

}
