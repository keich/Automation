package ru.keich.mon.automation.snmp.ui;

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

import ru.keich.mon.automation.snmp.SnmpScriptMapping;

public class SnmpRight extends VerticalLayout {
	
	private static final long serialVersionUID = 2414066206798222948L;
	
	public static final String SAVE_BUTTON_TEXT = "Save";
	
	public static final String DELETE_BUTTON_TEXT = "Delete";
	public static final String DELETE_DIALOG_TEXT = "Want to delete ";
	public static final String DELETE_DIALOG_YES = "Delete";
	public static final String DELETE_DIALOG_NO = "Cancel";
	
	public static final String FORM_NAME_TEXT = "Name";
	public static final String FORM_MATCHOID_TEXT = "Match OID";
	public static final String FORM_COMPARE_TEXT = "Compare OID";
	public static final String FORM_GENERICTRAP_TEXT = "Generic Trap";
	public static final String FORM_SCRIPTNAME_TEXT = "Script Name";
	
	public static final String CHECKBOX_ENABLE_TEXT = "Enable";
	
	private final Button saveButton;
	private final Button deleteButton;
	
	private final TextField name;
	private final TextField matchOid;
	private final TextField compareOid;
	private final ComboBox<String> scriptName;
	private final Checkbox enable;

	public SnmpRight(BackEndDataProvider<String, String> dataProvider, Consumer<SnmpScriptMapping> save, Consumer<SnmpScriptMapping> delete) {
		var formLayout = new FormLayout();
		formLayout.setAutoResponsive(true);
		formLayout.setWidthFull();
		
		name = new TextField(FORM_NAME_TEXT, this::validate);
		
		matchOid = new TextField(FORM_MATCHOID_TEXT, this::validate);
		
		compareOid = new TextField(FORM_COMPARE_TEXT,this::validate);

		
		scriptName = new ComboBox<String>(FORM_SCRIPTNAME_TEXT, this::validate);
		scriptName.setItems(dataProvider);
		
		formLayout.add(name, matchOid, compareOid, scriptName);

		add(formLayout);
		
		enable = new Checkbox(CHECKBOX_ENABLE_TEXT, e -> {
			e.getValue();
		});
		add(enable);
		
		saveButton = new Button(SAVE_BUTTON_TEXT, e -> {
			save.accept(getData());
		});
		saveButton.setEnabled(false);
		
		var deleteDialog = createDeleteDialog(() -> {
			delete.accept(getData());
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
	
	public boolean open(SnmpScriptMapping sm) {
		name.setValue(sm.getName());
		matchOid.setValue(sm.getMatchOid().toString());
		compareOid.setValue(sm.getCompareOid().toString());
		scriptName.setValue(sm.getScriptName());
		enable.setValue(sm.isEnable());
		return false;
	}
	
	public boolean add() {
		clear();
		deleteButton.setEnabled(false);
		return false;
	}
	
	private void clear() {
		name.clear();
		matchOid.clear();
		compareOid.clear();
		scriptName.clear();
		enable.setValue(false);
	}

	public SnmpScriptMapping getData() {
		return new SnmpScriptMapping().setName(name.getValue())
				.setMatchOid(matchOid.getValue())
				.setCompareOid(compareOid.getValue())
				.setScriptName(scriptName.getValue())
				.setEnable(enable.getValue());
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
	
	public void validate(Object event) {
		saveButton.setEnabled(getData().isValid());
	}
	
}
