package ru.keich.mon.automation.scripting;

import java.util.List;
import java.util.Map;

import ru.keich.mon.automation.dbdatasource.DBDataSourceService;

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
