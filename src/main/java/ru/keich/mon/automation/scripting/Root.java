package ru.keich.mon.automation.scripting;

import java.util.HashMap;
import java.util.Map;

import ru.keich.mon.automation.dbdatasource.DBDataSourceService;

public class Root {

	public static final String MEMBER_LOG_NAME = "log";
	public static final String MEMBER_DB_NAME = "db";

	public static Map<String, Object> getMembers(LogManager logm, DBDataSourceService dataSourceService) {
		var ret = new HashMap<String, Object>();
		ret.put(MEMBER_LOG_NAME, logm);
		ret.put(MEMBER_DB_NAME, new DBManager(dataSourceService));
		return ret;
	}

}
