package ru.keich.mon.automation.script.version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import com.github.difflib.text.DiffRow;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.Element;
import ru.keich.mon.automation.script.ui.ScriptNameDataProvider;

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
	
	public ScriptEditRightVersion(ScriptVersionService versionsService, ScriptNameDataProvider nameProvider, 
			Function<Instant, Optional<ScriptVersion>> getScriptVersionByInstant) 
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
				
				rows.addAll(gen.generateDiffRows(List.of(st.get(0).getCode()), List.of(st.get(1).getCode())));
			} catch(Exception ex) {
				System.err.println(ex);
			}
			clear();
			diffArea.setItems(rows);
		});
		var header = new Header();
		versionField = new ComboBox<String>();
		versionField.setItemLabelGenerator(i -> i.toString());
		versionField.setItems(nameProvider);
		versionField.addValueChangeListener(i -> {
			setScriptName(i.getValue());
			clear();
		});
		header.add(versionField);

		var layout = new VerticalLayout();
		grid = new Grid<ScriptVersion>();
		grid.addColumn(ScriptVersion::getId).setHeader("ID");
		grid.addColumn(ScriptVersion::getNote).setHeader("NOTE");
		grid.addComponentColumn((sc) -> {
			var btn = new Button("Reject");
			btn.addClickListener((evt)->{});
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
		grid.setItemsPageable(page->{
			return versionsService.findAllByName(page.getPageSize(), page.getPageNumber(), scriptName).toList();
		});

		grid.addItemDoubleClickListener((evnt) -> {

		});
		layout.add(grid);
		
		this.add(header);
		this.add(layout);
	}

}
