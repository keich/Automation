package ru.keich.mon.automation.datasource;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.keich.mon.automation.ui.simpleEdit.SimpleEditItem;
import ru.keich.mon.automation.ui.simpleEdit.SimpleEditRight.FormFieldBuiler;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class DataSource implements SimpleEditItem, Serializable {

	private static final long serialVersionUID = 1403705980553230016L;

	public static final String DB_NAME_ID = "DBNAME";
	public static final String TYPE_ID = "TYPE";
	public static final String DB_CLASS_ID = "DBCLASS";
	public static final String DB_URL_ID = "DBURL";
	public static final String DB_LOGIN_ID = "DBLOGIN";
	public static final String DB_PASS_ID = "DBPASS";
	public static final String REST_NAME_ID = "RESTNAME";
	public static final String REST_URLID = "RESTURL";
	public static final String REST_AUTH_ID = "RESTAUTH";

	public static final String NAME_FORM_NAME = "Name";
	public static final String TYPE_FORM_NAME = "Type";

	public static final String DB_CLASS_FORM_NAME = "Driver Class";
	public static final String DB_URL_FORM_NAME = "JDBC URL";
	public static final String DB_LOGIN_FORM_NAME = "User Name";
	public static final String DB_PASS_FORM_NAME = "Password";

	public static final String REST_URL_FORM_NAME = "URL";
	public static final String REST_AUTH_HEADER_FORM_NAME = "Authorization Header";

	@Id
	private String name = "";

	private DataSourceType type = DataSourceType.DB;

	private String dbClass = "";

	private String URL = "";

	private String login = "";

	private String password = "";

	@Override
	public boolean validate() {
		switch (type) {
		case DB:
			return !"".equals(name) && !"".equals(dbClass) && !"".equals(URL) && !"".equals(login)
					&& !"".equals(password);
		case REST:
			return !"".equals(name) && !"".equals(password);
		}
		return false;
	}

	@Override
	public FormFieldBuiler getFormFields() {
		return new FormFieldBuiler().addText(DB_NAME_ID, DataSourceType.DB.name(), NAME_FORM_NAME)
				.addText(DB_CLASS_ID, DataSourceType.DB.name(), DB_CLASS_FORM_NAME)
				.addText(DB_URL_ID, DataSourceType.DB.name(), DB_URL_FORM_NAME)
				.addText(DB_LOGIN_ID, DataSourceType.DB.name(), DB_LOGIN_FORM_NAME)
				.addPass(DB_PASS_ID, DataSourceType.DB.name(), DB_PASS_FORM_NAME)
				.addText(REST_NAME_ID, DataSourceType.REST.name(), NAME_FORM_NAME)
				.addText(REST_URLID, DataSourceType.REST.name(), REST_URL_FORM_NAME)
				.addText(REST_AUTH_ID, DataSourceType.REST.name(), REST_AUTH_HEADER_FORM_NAME);
	}

	@Override
	public Map<String, String> toMap() {
		var ret = new HashMap<String, String>();
		ret.put(DB_NAME_ID, name);
		ret.put(REST_NAME_ID, name);
		ret.put(TYPE_ID, type.name());
		ret.put(DB_CLASS_ID, dbClass);
		ret.put(DB_URL_ID, URL);
		ret.put(DB_LOGIN_ID, login);
		ret.put(DB_PASS_ID, password);
		ret.put(REST_URLID, login);
		ret.put(REST_AUTH_ID, password);
		return ret;
	}

	@Override
	public void fromMap(String type, Map<String, String> data) {
		if (type != null && !"".equals(type)) {
			this.setType(DataSourceType.valueOf(type));
		}
		if (this.getType() != null) {
			switch (this.getType()) {
			case DB:
				this.setName(data.get(DB_NAME_ID));
				this.setDbClass(data.get(DB_CLASS_ID));
				this.setURL(data.get(DB_URL_ID));
				this.setLogin(data.get(DB_LOGIN_ID));
				this.setPassword(data.get(DB_PASS_ID));
				break;
			case REST:
				this.setName(data.get(REST_NAME_ID));
				this.setURL(data.get(REST_URLID));
				this.setPassword(data.get(REST_AUTH_ID));
				break;
			}
		}
	}

	@Override
	public String getGroup() {
		return this.getType().name();
	}

	@Override
	public String toString() {
		return "DataSource [name=" + name + ", type=" + type + ", dbClass=" + dbClass + ", URL=" + URL + ", login="
				+ login + ", password=" + password + "]";
	}

}
