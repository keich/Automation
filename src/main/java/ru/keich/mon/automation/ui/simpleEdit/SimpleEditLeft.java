package ru.keich.mon.automation.ui.simpleEdit;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.ValueProvider;

public class SimpleEditLeft<T> extends VerticalLayout {

	private static final long serialVersionUID = -8573928733715194862L;

	private final Grid<T> grid;

	public SimpleEditLeft(DataProvider<T, Void> dataProvider, Consumer<T> open, Supplier<Boolean> addNew) {
		setSizeFull();
		setHeightFull();

		grid = new Grid<T>(dataProvider);
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

	public void addColumn(ValueProvider<T, ?> valueProvider) {
		grid.addColumn(valueProvider);
	}

}
