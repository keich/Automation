package ru.keich.mon.automation.script.ui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;

import ru.keich.mon.automation.script.Script;

public class ScriptsEditLeft extends VerticalLayout {

	private static final long serialVersionUID = -406569669034516329L;

	private final TreeGrid<Script> grid;

	public ScriptsEditLeft(ScriptHierarchicalDataProvider dataProvider, Consumer<Script> open,
			Supplier<Boolean> addNew) {
		grid = new TreeGrid<Script>();
		grid.addItemClickListener(e -> open.accept(e.getItem()));
		grid.addHierarchyColumn(Script::getName);
		grid.setDataProvider(dataProvider);

		var plusButton = new Button(new Icon(VaadinIcon.PLUS));
		plusButton.addClickListener(e -> addNew.get());

		var buttons = new HorizontalLayout();
		buttons.add(plusButton);

		add(buttons);
		add(grid);
	}

	public void refresh() {
		grid.getDataProvider().refreshAll();
	}

}
