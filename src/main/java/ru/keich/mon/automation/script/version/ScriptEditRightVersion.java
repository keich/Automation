package ru.keich.mon.automation.script.version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.difflib.text.DiffRow;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;

import ru.keich.mon.automation.script.ui.ScriptNameDataProvider;
import java.util.function.Supplier;
import java.util.stream.Stream;

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

public class ScriptEditRightVersion extends Div {
	record CheckBoxChoosenState(ScriptVersion sc, Consumer<Boolean> setCheckboxValue) {
		@Override
		public int hashCode() {
			return sc.hashCode();
		}
	}

	private static final long serialVersionUID = 9051167217283860982L;
	private String scriptName;
	private final ComboBox<String> versionField;
	private final Grid<ScriptVersion> grid;
	private final Dialog diffDialog;
	private final Button diffBtn;
	private final List<CheckBoxChoosenState> choosenScripts;

	public void setScriptName(String name) {
		this.scriptName = name;
		versionField.setValue(name);
		grid.getDataProvider().refreshAll();
	} 
	
	private void clear() {
		choosenScripts.stream().forEach((i)->i.setCheckboxValue.accept(false));
		choosenScripts.clear();
		diffBtn.setEnabled(false);
	}
	
	public void setStyle() {
		Element el = new Element("style");
		el.setText("""
				vaadin-grid::part(grid-insert) { background-color: #dcfce7; color: #166534; }
				vaadin-grid::part(grid-delete) { background-color: #fee2e2; color: #991b1b; }
				vaadin-grid::part(grid-change) { background-color: #fef08a; color: #854d0e; }
				""");
		getElement().appendChild(el);
	}
	
	Instant currentId;
	public ScriptEditRightVersion(ScriptVersionService versionsService, ScriptNameDataProvider nameProvider, 
			Function<Instant, Optional<ScriptVersion>> getScriptVersionByInstant, Supplier<Boolean> setContent, Consumer<ScriptVersion> setScript) 
	{
		setStyle();
		choosenScripts = new ArrayList<>();
		
		diffDialog = new Dialog();
		diffDialog.setWidth("80%");
		diffDialog.setHeight("70%");
		var diffArea = new Grid<DiffRow>();
		diffArea.addColumn(DiffRow::getTag)
		.setHeader("");
		diffArea.addColumn(DiffRow::getNewLine)
		.setHeader("");
		diffArea.addColumn(DiffRow::getOldLine)
		.setHeader("");
		diffArea.setPartNameGenerator((i)->{
			switch (i.getTag()) {
				case INSERT:
					return "grid-insert";
				case DELETE:
					return "grid-delete";
				case CHANGE:
					return "grid-change";
				default:
					return null;
			}
		});
		
		diffDialog.add(diffArea);
		
		diffBtn = new Button("Diff");
		diffBtn.setEnabled(false);
		var gen = DiffRowGeneratorVersions.build();
		diffBtn.addClickListener((evnt)->{
			List<DiffRow> rows = new ArrayList<>();
			try {
				var st = choosenScripts.stream()
										.map(CheckBoxChoosenState::sc)
										.sorted()
										.toList();
				
				rows.addAll(gen.generateDiffRows(Arrays.asList(st.get(0).getCode().split("\n")), Arrays.asList(st.get(1).getCode().split("\n"))));
			} catch(Exception ex) {
				System.err.println(ex);
			}
			clear();
			diffArea.setItems(rows);
			diffDialog.open();
		});
		var header = new Header();
		versionField = new ComboBox<String>();
		versionField.setItemLabelGenerator(i -> i.toString());
		versionField.setItems(nameProvider);
		versionField.addValueChangeListener(i -> {
			setScriptName(i.getValue());
			clear();
		});
		versionField.getStyle().set("padding-left","15px");
		header.add(versionField);

		var layout = new VerticalLayout();
		
		grid = new Grid<ScriptVersion>();
		grid.setItemsPageable(page->{
			var data = versionsService.findAllByName(page.getPageSize(), page.getPageNumber(), scriptName).sorted(Comparator.reverseOrder()).toList();
			if (!data.isEmpty())
				currentId = data.get(0).getId();
			return data;
		});
		grid.addComponentColumn(el->{
			var text = new Span(el.getId().toString());
			if (el.getId().equals(currentId)) {
				text.setText("current");
				text.getStyle()
					.set("background-color","#C0C0E2")
					.set("border","2px, solid, #333381")
					.set("border-radius","25px")
					.set("padding", "3px")
					.set("color", "#333381")
					.set("box-shadow","0px 0px 10px #52529C");
			}
			
			return text;
		}).setHeader("").setTextAlign(ColumnTextAlign.CENTER).getStyle().set("padding","10px");
		
		grid.addColumn(ScriptVersion::getNote).setHeader("NOTE");
		Consumer<ScriptVersion> sendToIde = (sc) -> {
			setScript.accept(sc);
			setContent.get();
		};
		grid.addComponentColumn((sc) -> {
			var btn = new Button("Send To IDE");
			btn.addClickListener((evt)->
				sendToIde.accept(sc)
			);
			return btn;
		}).setHeader("");
		
		grid.addComponentColumn((sc) -> {
			var chk = new Checkbox();
			chk.addClickListener((evnt) -> {
				if (evnt.getSource().getValue()) {
					if (choosenScripts.size() < 2) 
						choosenScripts.add(new CheckBoxChoosenState(sc, chk::setValue));
					else
						evnt.getSource().setValue(false);
				} else {
					choosenScripts.removeIf((i) -> i.sc().equals(sc));
				}
				diffBtn.setEnabled(choosenScripts.size() == 2);
			});
			return chk;
		}).setHeader(diffBtn);
		
		grid.addItemDoubleClickListener((evnt) -> 
			sendToIde.accept(evnt.getItem())
		);
		layout.add(grid);
		
		this.add(header);
		this.add(layout);
	}
}
