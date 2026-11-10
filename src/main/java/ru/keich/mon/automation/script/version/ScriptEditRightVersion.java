package ru.keich.mon.automation.script.version;

import java.time.Instant;
import java.util.Optional;
import java.util.function.Function;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.Query;

import ru.keich.mon.automation.script.ui.ScriptNameDataProvider;
import ru.keich.mon.automation.script.ui.ScriptVersionDataProvider;

public class ScriptEditRightVersion extends Div {
	private String scriptName;
	private final ComboBox<String> versionField;
	private final Grid<ScriptVersion> grid;
	
	public void setScriptName(String name) {
		this.scriptName = name;
		versionField.setValue(name);
		grid.getDataProvider().refreshAll();
	} 
	
	public ScriptEditRightVersion(ScriptVersionService versionsService, ScriptNameDataProvider nameProvider, 
			Function<Instant, Optional<ScriptVersion>> getScriptVersionByInstant  ) 
	{
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
		grid.setItemsPageable(page->{
			return versionsService.findAllByName(page.getPageSize(), page.getPageNumber(), scriptName).toList();
		});
		grid.addItemDoubleClickListener((evnt)->{
			
		});
		layout.add(grid);
		this.add(header);
		this.add(layout);
	}
}