package ru.keich.mon.automation.script;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import ru.keich.mon.automation.dbdatasource.DBDataSourceService;
import ru.keich.mon.automation.httpdatasource.HttpDataSourceService;
import ru.keich.mon.automation.schedule.ScheduleService;
import ru.keich.mon.automation.scripting.ScriptCallBack;
import ru.keich.mon.automation.snmp.SnmpService;

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

@Service
public class ScriptService {

	private final ScriptRepository scriptRepository;
	private final DBDataSourceService dataSourceService;
	private final SnmpService snmpService;
	private final HttpDataSourceService httpDataSourceService;

	public ScriptService(ScriptRepository scriptRepository, DBDataSourceService dataSourceService, SnmpService snmpService, HttpDataSourceService httpDataSourceService) {
		this.scriptRepository = scriptRepository;
		this.dataSourceService = dataSourceService;
		this.snmpService = snmpService;
		this.httpDataSourceService = httpDataSourceService;
	}
	
	public void setScheduleService(ScheduleService scheduleService) {
		snmpService.setScheduleService(scheduleService);
	}

	public Stream<Script> getAllOrRoot(HierarchicalQuery<Script, Void> q) {
		return getByParent(q.getParentOptional().map(Script::getName)).stream().skip(q.getOffset()).limit(q.getLimit());
	}

	public int getCountOrRoot(HierarchicalQuery<Script, Void> q) {
		return Math.toIntExact(getByParent(q.getParentOptional().map(Script::getName)).stream().skip(q.getOffset())
				.limit(q.getLimit()).count());
	}

	public Stream<String> getAll(Query<String, String> q) {
		return getByNameContaing(q.getFilter()).stream().skip(q.getOffset()).limit(q.getLimit()).map(Script::getName);
	}

	public int getCount(Query<String, String> q) {
		return Math
				.toIntExact(getByNameContaing(q.getFilter()).stream().skip(q.getOffset()).limit(q.getLimit()).count());
	}

	public List<Script> getByNameContaing(Optional<String> filter) {
		return filter.map(scriptRepository::findByNameContainingIgnoreCase).orElse(scriptRepository.findAll());
	}
	
	public Optional<Script> getByName(String name) {
		return scriptRepository.findById(name);
	}

	public List<Script> getByParent(Optional<String> opt) {
		return opt.map(scriptRepository::findByParent).orElse(scriptRepository.findRoot());
	}

	public List<Script> getChild(String scriptName) {
		return scriptRepository.findByParent(scriptName);
	}

	public int getChildCountOrRoot(Optional<String> parent) {
		return getByParent(parent).size();
	}

	public void save(Script script) {
		scriptRepository.save(script);
	}

	public void delete(Script script) {
		scriptRepository.delete(script);
	}

	public void run(String name, Object param, ScriptCallBack callBack) {
		scriptRepository.findById(name).ifPresentOrElse(script -> {
			run(script, param, callBack);
		}, () -> { 
			callBack.onError(new RuntimeException("Script not found"));
		});
	}
	
	public void run(Script script, Object param, ScriptCallBack callBack)  {
		var scriptContext = new ScriptContext(dataSourceService, this, snmpService, httpDataSourceService);
		scriptContext.setLogCallBack(callBack::onLog);
		Map<String, Object> result = Collections.emptyMap();
		try {
			result = scriptContext.run(script, param);
			if(result.containsKey(ScriptResult.KEY_RESULT)) {
				callBack.onResult(result.get(ScriptResult.KEY_RESULT).toString());
			} else {
				callBack.onError(new RuntimeException(result.get(ScriptResult.KEY_ERR).toString()));
			}
		} catch (Exception e){
			callBack.onError(e);
		} finally {
			scriptContext.close();
		}
	}

}
