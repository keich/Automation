package ru.keich.mon.automation.schedule.ui;

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

public class ScheduleLeft extends VerticalLayout {

	private static final long serialVersionUID = 3922455091445463138L;

	private final Grid<Schedule> grid;

	public ScheduleLeft(DataProvider<Schedule, Void> dataProvider, Consumer<Schedule> open, Supplier<Boolean> addNew) {

		grid = new Grid<Schedule>(dataProvider);
		grid.addColumn(Schedule::getName);
		grid.addComponentColumn(s -> {
			if(s.isEnable()) {
				var ico = new Icon(VaadinIcon.REFRESH);
				ico.setSize("1em");
				return ico;
			} 
			return new Div();
		}).setFlexGrow(0).setWidth("4em");
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
