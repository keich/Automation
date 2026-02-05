package ru.keich.mon.automation.dbdatasource;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	private Integer maximumPoolSize = 10;

	public boolean isValid() {
		return name != null && dbClass != null && URL != null && login != null && !"".equals(name)
				&& !"".equals(dbClass) && !"".equals(URL) && !"".equals(login) &&  maximumPoolSize != null && maximumPoolSize > 0;
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
	
	public DBDataSource setMaximumPoolSize(Integer maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
		return this;
	}
	
	@Override
	public String toString() {
		return "DataSource [name=" + name + ", dbClass=" + dbClass + ", URL=" + URL + ", login="
				+ login + ", password=" + password + "]";
	}

}
