package ru.keich.mon.automation.httplistner.ui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.BackEndDataProvider;

import ru.keich.mon.automation.httplistner.HttpListner;

public class HttpListnerEditRight extends VerticalLayout {

	private static final long serialVersionUID = -7077924600087672437L;

	private final Button saveButton;
	private final Button deleteButton;

	public static final String SAVE_BUTTON_TEXT = "Save";
	public static final String DELETE_BUTTON_TEXT = "Delete";

	public static final String FORM_NAME_TEXT = "Name";
	public static final String FORM_PATH_TEXT = "Path";
	public static final String FORM_SCRIPTNAME_TEXT = "Script Name";

	public static final String DELETE_DIALOG_TEXT = "Want to delete ";
	public static final String DELETE_DIALOG_YES = "Delete";
	public static final String DELETE_DIALOG_NO = "Cancel";

	public static final String CHECKBOX_ENABLE_TEXT = "Enable";
	public static final String CHECKBOX_PERMITALL_TEXT = "Permit All Access";

	public static final String SCRIPTNAME_TEXT = "Script name";
	public static final String CONTENTTYPE_TEXT = "Content Type";

	private final TextField path;
	private final ComboBox<String> scriptName;
	private final ComboBox<HttpListner.ContentType> contentType;
	
	private final Checkbox enable;
	private final Checkbox permitAllAccess;

	public HttpListnerEditRight(BackEndDataProvider<String, String> scriptNameDataProvider, Consumer<HttpListner> save,	Consumer<HttpListner> delete) {
		var formLayout = new FormLayout();
		formLayout.setAutoResponsive(true);
		formLayout.setWidthFull();

		path = new TextField(FORM_PATH_TEXT, this::validate);

		scriptName = new ComboBox<String>(SCRIPTNAME_TEXT, this::validate);
		scriptName.setItems(scriptNameDataProvider);

		enable = new Checkbox(CHECKBOX_ENABLE_TEXT, this::validate);
		permitAllAccess = new Checkbox(CHECKBOX_PERMITALL_TEXT, this::validate);

		contentType = new ComboBox<HttpListner.ContentType>(CONTENTTYPE_TEXT, this::validate);
		contentType.setItems(HttpListner.ContentType.values());

		formLayout.add(path, scriptName, contentType, permitAllAccess, enable);

		add(formLayout);

		saveButton = new Button(SAVE_BUTTON_TEXT, e -> {
			save.accept(getHttpListner());
		});
		saveButton.setEnabled(false);

		var deleteDialog = createDeleteDialog(() -> {
			delete.accept(getHttpListner());
			clear();
			return false;
		});

		deleteButton = new Button(DELETE_BUTTON_TEXT, e -> {
			deleteDialog.setHeaderTitle(DELETE_DIALOG_TEXT + path.getValue() + "?");
			deleteDialog.open();
		});
		deleteButton.setEnabled(false);

		var buttons = new HorizontalLayout();
		buttons.add(saveButton);
		buttons.add(deleteButton);

		add(buttons);
	}

	private void validate(Object event) {
		saveButton.setEnabled(getHttpListner().isValid());
	}

	private HttpListner getHttpListner() {
		return new HttpListner().setPath(path.getValue()).setScriptName(scriptName.getValue())
				.setEnable(enable.getValue()).setContentType(contentType.getValue()).setPermitAllAccess(permitAllAccess.getValue());
	}

	public boolean open(HttpListner httpListner) {
		path.setValue(httpListner.getPath());
		scriptName.setValue(httpListner.getScriptName());
		enable.setValue(httpListner.isEnable());
		deleteButton.setEnabled(true);
		contentType.setValue(httpListner.getContentType());
		permitAllAccess.setValue(httpListner.isPermitAllAccess());
		return false;
	}

	public boolean add() {
		clear();
		deleteButton.setEnabled(false);
		return false;
	}

	private void clear() {
		path.clear();
		enable.clear();
		scriptName.clear();
		permitAllAccess.clear();
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
