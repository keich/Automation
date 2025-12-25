package ru.keich.mon.automation.httplistner.ui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.CallbackDataProvider;

import ru.keich.mon.automation.httplistner.HttpListner;

public class HttpListnerEditLeft extends VerticalLayout {

	private static final long serialVersionUID = 8500529323013473213L;

	private final Grid<HttpListner> grid;

	public HttpListnerEditLeft(CallbackDataProvider<HttpListner, Void> dataProvider, Consumer<HttpListner> open, Supplier<Boolean> addNew) {
		grid = new Grid<HttpListner>(dataProvider);
		grid.addColumn(HttpListner::getPath);
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
