package ru.keich.mon.automation.schedule.ui;

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

import ru.keich.mon.automation.schedule.Schedule;

public class ScheduleRight extends VerticalLayout {

	private static final long serialVersionUID = 8004233119251812423L;

	public static final String SAVE_BUTTON_TEXT = "Save";
	public static final String DELETE_BUTTON_TEXT = "Delete";

	public static final String CHECKBOX_ENABLE_TEXT = "Enable";
	public static final String FORM_NAME_TEXT = "Name";
	public static final String FORM_EXPRESSION_TEXT = "Expression";
	public static final String COMBOBOX_TEXT = "Script name";

	public static final String DELETE_DIALOG_TEXT = "Want to delete ";
	public static final String DELETE_DIALOG_YES = "Delete";
	public static final String DELETE_DIALOG_NO = "Cancel";

	private final Button saveButton;
	private final Button deleteButton;

	private final TextField name;
	private final TextField expression;
	private final ComboBox<String> scriptName;

	private final Checkbox enable;

	public ScheduleRight(BackEndDataProvider<String, String> scriptNameDataProvider, Consumer<Schedule> save, Consumer<Schedule> delete) {
		var formLayout = new FormLayout();
		formLayout.setAutoResponsive(true);
		formLayout.setWidthFull();

		name = new TextField(FORM_NAME_TEXT, this::validate);

		expression = new TextField(FORM_EXPRESSION_TEXT, this::validate);

		formLayout.add(name, expression);

		add(formLayout);

		saveButton = new Button(SAVE_BUTTON_TEXT, e -> {
			save.accept(getSchedule());
		});
		saveButton.setEnabled(false);

		var deleteDialog = createDeleteDialog(() -> {
			delete.accept(getSchedule());
			clear();
			return false;
		});

		deleteButton = new Button(DELETE_BUTTON_TEXT, e -> {
			deleteDialog.setHeaderTitle(DELETE_DIALOG_TEXT + name.getValue() + "?");
			deleteDialog.open();
		});

		deleteButton.setEnabled(false);

		scriptName = new ComboBox<String>(COMBOBOX_TEXT, this::validate);
		scriptName.setItems(scriptNameDataProvider);
		
		add(scriptName);
		
		enable = new Checkbox(CHECKBOX_ENABLE_TEXT, this::validate);
		add(enable);

		var buttons = new HorizontalLayout();
		buttons.add(saveButton);
		buttons.add(deleteButton);

		add(buttons);

	}

	private void validate(Object event) {
		saveButton.setEnabled(getSchedule().isValid());
	}

	private Schedule getSchedule() {
		return new Schedule()
				.setExpression(expression.getValue())
				.setName(name.getValue())
				.setScriptName(scriptName.getValue())
				.setEnable(enable.getValue());
	}

	public boolean open(Schedule schedule) {
		name.setValue(schedule.getName());
		expression.setValue(schedule.getExpression());
		enable.setValue(schedule.isEnable());
		deleteButton.setEnabled(true);
		scriptName.setValue(schedule.getScriptName());
		return false;
	}

	private void clear() {
		name.clear();
		expression.clear();
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
