package ru.keich.mon.automation.ui.simpleEdit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;

import lombok.Getter;
import lombok.Setter;

public class SimpleEditRight extends VerticalLayout {

	private static final long serialVersionUID = 4398287982430581748L;

	public static final String FORM_NAME = "Name";
	public static final String FORM_TYPE = "Type";
	public static final String SAVE_BUTTON_TEXT = "Save";
	public static final String DELETE_BUTTON_TEXT = "Delete";

	private final FormLayout formLayout = new FormLayout();
	private final Button saveButton;
	private final Button deleteButton;
	private final Select<String> select;

	private final LinkedHashMap<String, FormFieldContainer> fields = new LinkedHashMap<>();

	private final BiFunction<String, Map<String, String>, Boolean> validate;

	public enum FormFieldType {
		TEXT, PASS
	}

	@Getter
	@Setter
	public static class FormFieldContainer {
		AbstractField<?, String> component;
		FormFieldType fieldType;
		String name;
		String group;

		public FormFieldContainer(String group, String name, FormFieldType type) {
			super();
			this.group = group;
			this.fieldType = type;
			this.name = name;
		}

	}

	@Getter
	public static class FormFieldBuiler {

		private final LinkedHashMap<String, FormFieldContainer> fields = new LinkedHashMap<>();
		private final HashSet<String> groups = new HashSet<>();

		public FormFieldBuiler addText(String key, String groupType, String name) {
			return add(key, groupType, name, FormFieldType.TEXT);
		}

		public FormFieldBuiler addPass(String key, String groupType, String name) {
			return add(key, groupType, name, FormFieldType.PASS);
		}

		public FormFieldBuiler add(String key, String groupType, String name, FormFieldType type) {
			fields.put(key, new FormFieldContainer(groupType, name, type));
			groups.add(groupType);
			return this;
		}

	}

	public SimpleEditRight(BiConsumer<String, Map<String, String>> save, BiConsumer<String, Map<String, String>> delete,
			BiFunction<String, Map<String, String>, Boolean> validate) {
		super();
		this.validate = validate;
		this.setWidthFull();

		select = new Select<>(FORM_TYPE, selectEvent -> {
			reflash();
		});
		select.setReadOnly(true);

		saveButton = new Button(SAVE_BUTTON_TEXT, e -> {
			save.accept(select.getValue(), toMap());
		});
		saveButton.setEnabled(false);

		deleteButton = new Button(DELETE_BUTTON_TEXT, e -> {
			delete.accept(select.getValue(), toMap());
		});
		deleteButton.setEnabled(false);

		formLayout.setAutoResponsive(true);
		formLayout.setWidthFull();

		formLayout.addFormRow(select);

		add(formLayout);

		var buttons = new HorizontalLayout();
		buttons.add(saveButton);
		buttons.add(deleteButton);

		add(buttons);

	}

	private void reflash() {
		var group = select.getValue();
		if (group != null && !"".equals(group)) {
			fields.forEach((key, item) -> {
				if (group.equals(item.getGroup())) {
					item.getComponent().setVisible(true);
				} else {
					item.getComponent().setVisible(false);
				}
			});
		}
	}

	public void updateFormFields(FormFieldBuiler formFieldBuiler) {
		select.clear();
		fields.clear();
		formLayout.removeAll();
		formLayout.addFormRow(select);
		select.setItems(formFieldBuiler.getGroups());
		formFieldBuiler.getFields().forEach((key, item) -> {
			switch (item.getFieldType()) {
			case TEXT:
				var t = new TextField(item.getName());
				t.addValueChangeListener(e -> {
					validate();
				});
				t.setVisible(false);
				item.setComponent(t);
				break;
			case PASS:
				var p = new PasswordField(item.getName());
				p.setRevealButtonVisible(false);
				p.setVisible(false);
				p.addValueChangeListener(e -> {
					validate();
				});
				item.setComponent(p);
				break;
			}
			fields.put(key, item);
			formLayout.add(item.getComponent());
		});
		reflash();
	}

	private void validate() {
		saveButton.setEnabled(validate.apply(select.getValue(), toMap()));
	}

	private void clearAll() {
		select.clear();
		fields.forEach((key, item) -> {
			item.getComponent().clear();
			item.getComponent().setVisible(false);
		});
	}

	public boolean addNew() {
		select.setReadOnly(false);
		saveButton.setEnabled(false);
		deleteButton.setEnabled(false);
		clearAll();
		return true;
	}

	public void open(String group, Map<String, String> data) {
		select.setReadOnly(true);
		clearAll();
		select.setValue(group);
		fields.forEach((key, item) -> {
			if (data.containsKey(key)) {
				item.getComponent().setValue(data.get(key));
			}
		});
		deleteButton.setEnabled(true);
	}

	private Map<String, String> toMap() {
		var ret = new HashMap<String, String>();
		fields.forEach((key, item) -> {
			ret.put(key, item.getComponent().getValue());
		});
		return ret;
	}

}
