package ru.keich.mon.automation.dbdatasource.ui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;

import ru.keich.mon.automation.dbdatasource.DBDataSource;

public class DBDataSourceLeft extends VerticalLayout {

	private static final long serialVersionUID = 3922455091445463138L;

	private final Grid<DBDataSource> grid;

	public DBDataSourceLeft(DataProvider<DBDataSource, Void> dataProvider, Consumer<DBDataSource> open, Supplier<Boolean> addNew) {

		grid = new Grid<DBDataSource>(dataProvider);
		grid.addColumn(DBDataSource::getName);
		grid.addItemClickListener(e -> open.accept(e.getItem()));
		grid.setSizeFull();
		grid.setHeightFull();

		var plusButton = new Button(new Icon(VaadinIcon.PLUS));
		plusButton.addClickListener(e -> addNew.get());

		var buttons = new HorizontalLayout();
		buttons.add(plusButton);

		this.add(buttons);
		this.add(grid);
	}
	
	public void refresh() {
		grid.getDataProvider().refreshAll();
	}

}
