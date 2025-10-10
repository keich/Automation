package ru.keich.mon.automation.ui.simpleEdit;

import java.util.Map;

import ru.keich.mon.automation.ui.simpleEdit.SimpleEditRight.FormFieldBuiler;

public interface SimpleEditItem {

	public boolean validate();

	public String getGroup();

	public Map<String, String> toMap();

	public void fromMap(String type, Map<String, String> data);

	public FormFieldBuiler getFormFields();

}
