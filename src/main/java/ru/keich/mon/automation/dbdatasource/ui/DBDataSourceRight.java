package ru.keich.mon.automation.dbdatasource.ui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

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

public class DBDataSourceRight extends VerticalLayout {

	private static final long serialVersionUID = 8004233119251812423L;

	public static final String SAVE_BUTTON_TEXT = "Save";
	public static final String DELETE_BUTTON_TEXT = "Delete";

	public static final String FORM_NAME_TEXT = "Name";
	public static final String FORM_CLASS_TEXT = "Driver Class";
	public static final String FORM_URL_TEXT = "JDBC URL";
	public static final String FORM_LOGIN_TEXT = "User Name";
	public static final String FORM_PASS_TEXT = "Password";
	public static final String FORM_POOLSIZE_TEXT = "Maximum Pool Size";
	

	public static final String DELETE_DIALOG_TEXT = "Want to delete ";
	public static final String DELETE_DIALOG_YES = "Delete";
	public static final String DELETE_DIALOG_NO = "Cancel";

	private final Button saveButton;
	private final Button deleteButton;

	private final TextField name;
	private final TextField dricerClass;
	private final TextField jdbcURL;
	private final TextField login;
	private final PasswordField password;
	private final NumberField maximumPoolSize;

	public DBDataSourceRight(Consumer<DBDataSource> save, Consumer<DBDataSource> delete) {
		var formLayout = new FormLayout();
		formLayout.setAutoResponsive(true);
		formLayout.setWidthFull();

		name = new TextField(FORM_NAME_TEXT, this::validate);

		dricerClass = new TextField(FORM_CLASS_TEXT, this::validate);

		jdbcURL = new TextField(FORM_URL_TEXT, this::validate);

		login = new TextField(FORM_LOGIN_TEXT, this::validate);

		password = new PasswordField(FORM_PASS_TEXT, this::validate);
		password.setRevealButtonVisible(false);
		
		maximumPoolSize = new NumberField(FORM_POOLSIZE_TEXT);
		
		formLayout.add(name, dricerClass, jdbcURL, login, password, maximumPoolSize);

		add(formLayout);

		saveButton = new Button(SAVE_BUTTON_TEXT, e -> {
			save.accept(getDBDataSource());
		});
		saveButton.setEnabled(false);

		var deleteDialog = createDeleteDialog(() -> {
			delete.accept(getDBDataSource());
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
		saveButton.setEnabled(getDBDataSource().isValid());
	}

	private DBDataSource getDBDataSource() {
		return new DBDataSource()
				.setName(name.getValue())
				.setDbClass(dricerClass.getValue())
				.setURL(jdbcURL.getValue())
				.setLogin(login.getValue())
				.setPassword(password.getValue())
				.setMaximumPoolSize(maximumPoolSize.getValue() != null ? maximumPoolSize.getValue().intValue() : null);
	}

	public boolean open(DBDataSource dbDataSource) {
		name.setValue(dbDataSource.getName());
		dricerClass.setValue(dbDataSource.getDbClass());
		jdbcURL.setValue(dbDataSource.getURL());
		login.setValue(dbDataSource.getLogin());
		password.setValue(dbDataSource.getPassword());
		deleteButton.setEnabled(true);
		maximumPoolSize.setValue(Double.valueOf(dbDataSource.getMaximumPoolSize()));
		return false;
	}

	private void clear() {
		name.clear();
		dricerClass.clear();
		jdbcURL.clear();
		login.clear();
		password.clear();
		maximumPoolSize.clear();
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
