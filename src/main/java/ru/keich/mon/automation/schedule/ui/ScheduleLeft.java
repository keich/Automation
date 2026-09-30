package ru.keich.mon.automation.schedule.ui;
import ru.keich.mon.automation.schedule.ScheduleService;
import com.vaadin.flow.component.html.Span;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;

import ru.keich.mon.automation.schedule.Schedule;
import ru.keich.mon.automation.schedule.ScheduleService;

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

public class ScheduleLeft extends VerticalLayout {

	private static final long serialVersionUID = 3922455091445463138L;
	private final ScheduleService scheduleService;
	private final Grid<Schedule> grid;

	public ScheduleLeft(DataProvider<Schedule, Void> dataProvider, Consumer<Schedule> open, Supplier<Boolean> addNew, ScheduleService scheduleService) {
		this.scheduleService = scheduleService;
		grid = new Grid<Schedule>(dataProvider);
		grid.addColumn(Schedule::getName);
		grid.addComponentColumn(s -> {
			if(scheduleService.isRunning(s)) {
				var icon = new Icon(VaadinIcon.REFRESH);
				var text = new Span ("Выполняется");
				
				var status = new HorizontalLayout(icon,text);
				status.setAlignItems(Alignment.CENTER);
				
				status.setSpacing(true);
				return status;
						}
			var icon = new Icon(VaadinIcon.CLOCK);
			var text = new Span("Ожидание");
			var status = new HorizontalLayout(icon,text);
			
			status.setAlignItems(Alignment.CENTER);
			
			status.setSpacing(true);
			return status;
		}).setHeader("Статус").setWidth("10 em").setFlexGrow(0);	
			
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
