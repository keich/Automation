package ru.keich.mon.automation.dbdatasource;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DBDataSource {

	@Id
	private String name = "";

	private String dbClass = "";

	private String URL = "";

	private String login = "";

	private String password = "";

	public boolean isValid() {
		return name != null && dbClass != null && URL != null && login != null && !"".equals(name)
				&& !"".equals(dbClass) && !"".equals(URL) && !"".equals(login);
	}
	
	public DBDataSource setName(String name) {
		this.name = name;
		return this;
	}

	public DBDataSource setDbClass(String dbClass) {
		this.dbClass = dbClass;
		return this;
	}

	public DBDataSource setURL(String uRL) {
		URL = uRL;
		return this;
	}

	public DBDataSource setLogin(String login) {
		this.login = login;
		return this;
	}

	public DBDataSource setPassword(String password) {
		this.password = password;
		return this;
	}
	
	@Override
	public String toString() {
		return "DataSource [name=" + name + ", dbClass=" + dbClass + ", URL=" + URL + ", login="
				+ login + ", password=" + password + "]";
	}

}
