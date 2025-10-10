package ru.keich.mon.automation.scripting;

import java.util.List;
import java.util.Map;

import ru.keich.mon.automation.datasource.DataSourceService;

public class DBManager {

	private final DataSourceService dataSourceService;

	public DBManager(DataSourceService dataSourceService) {
		this.dataSourceService = dataSourceService;
	}

	public List<Map<String, Object>> queryForList(String name, String sql) {
		return dataSourceService.queryForList(name, sql);
	}

}
