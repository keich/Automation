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
		grid.getUI().ifPresent(ui -> {
			ui.access(() -> {
				grid.getDataProvider().refreshAll();
			});
		});
	}

}
