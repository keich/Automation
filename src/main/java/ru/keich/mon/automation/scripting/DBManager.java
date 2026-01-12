package ru.keich.mon.automation.scripting;

import java.util.List;
import java.util.Map;

import ru.keich.mon.automation.dbdatasource.DBDataSourceService;

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

public class DBManager {

	private final DBDataSourceService dataSourceService;

	public DBManager(DBDataSourceService dataSourceService) {
		this.dataSourceService = dataSourceService;
	}

	public List<Map<String, Object>> queryForList(String dataSourceName, String sql) {
		return dataSourceService.queryForList(dataSourceName, sql);
	}
	
	public int update(String dataSourceName, String sql, Object[] args) {
		return dataSourceService.update(dataSourceName, sql, args);
	}
	
}
