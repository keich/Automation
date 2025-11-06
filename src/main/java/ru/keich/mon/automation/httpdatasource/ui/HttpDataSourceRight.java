package ru.keich.mon.automation.httpdatasource.ui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import ru.keich.mon.automation.httpdatasource.HttpDataSource;

public class HttpDataSourceRight extends VerticalLayout {
	
	private static final long serialVersionUID = -6559496135404489040L;

	public static final String SAVE_BUTTON_TEXT = "Save";
	public static final String DELETE_BUTTON_TEXT = "Delete";

	public static final String FORM_NAME_TEXT = "Name";
	public static final String FORM_BASEURL_TEXT = "Base Url";	

	public static final String DELETE_DIALOG_TEXT = "Want to delete ";
	public static final String DELETE_DIALOG_YES = "Delete";
	public static final String DELETE_DIALOG_NO = "Cancel";

	private final Button saveButton;
	private final Button deleteButton;

	private final TextField name;
	private final TextField baseUrl;

	public HttpDataSourceRight(Consumer<HttpDataSource> save, Consumer<HttpDataSource> delete) {
		var formLayout = new FormLayout();
		formLayout.setAutoResponsive(true);
		formLayout.setWidthFull();

		name = new TextField(FORM_NAME_TEXT, this::validate);

		baseUrl = new TextField(FORM_BASEURL_TEXT, this::validate);
		
		formLayout.add(name, baseUrl);

		add(formLayout);

		saveButton = new Button(SAVE_BUTTON_TEXT, e -> {
			save.accept(getHttpDataSource());
		});
		saveButton.setEnabled(false);

		var deleteDialog = createDeleteDialog(() -> {
			delete.accept(getHttpDataSource());
			clear();
			return false;
		});

		deleteButton = new Button(DELETE_BUTTON_TEXT, e -> {
			deleteDialog.setHeaderTitle(DELETE_DIALOG_TEXT + name.getValue() + "?");
			deleteDialog.open();
		});
		deleteButton.setEnabled(false);

		var buttons = new HorizontalLayout();
		buttons.add(saveButton);
		buttons.add(deleteButton);

		add(buttons);

	}

	private void validate(Object event) {
		saveButton.setEnabled(getHttpDataSource().isValid());
	}

	private HttpDataSource getHttpDataSource() {
		return new HttpDataSource()
				.setName(name.getValue())
				.setBaseUrl(baseUrl.getValue());
	}

	public boolean open(HttpDataSource dataSource) {
		name.setValue(dataSource.getName());
		baseUrl.setValue(dataSource.getBaseUrl());
		deleteButton.setEnabled(true);
		return false;
	}

	private void clear() {
		name.clear();
		baseUrl.clear();
	}

	public boolean add() {
		clear();
		deleteButton.setEnabled(false);
		return false;
	}

	private static Dialog createDeleteDialog(Supplier<Boolean> doOnDelet) {
		var dialog = new Dialog();
		var yesBtn = new Button(DELETE_DIALOG_YES, e -> {
			doOnDelet.get();
			dialog.close();
		});
		yesBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		var noBtn = new Button(DELETE_DIALOG_NO, e -> {
			dialog.close();
		});
		dialog.getFooter().add(yesBtn, noBtn);
		return dialog;
	}
	
}
