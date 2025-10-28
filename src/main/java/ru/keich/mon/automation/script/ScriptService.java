package ru.keich.mon.automation.script;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import lombok.extern.java.Log;
import ru.keich.mon.automation.dbdatasource.DBDataSourceService;
import ru.keich.mon.automation.scripting.LogManager;
import ru.keich.mon.automation.scripting.LogManager.Line;
import ru.keich.mon.automation.snmp.SnmpService;

@Service
@Log
public class ScriptService {

	private final ScriptRepository scriptRepository;
	private final DBDataSourceService dataSourceService;
	private final SnmpService snmpService;

	public ScriptService(ScriptRepository scriptRepository, DBDataSourceService dataSourceService, SnmpService snmpService) {
		this.scriptRepository = scriptRepository;
		this.dataSourceService = dataSourceService;
		this.snmpService = snmpService;
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

	public void run(String name) {
		scriptRepository.findById(name)
				.ifPresent(script -> {
					run(script, l -> {});
				});
	}
	
	public void run(Script script, Consumer<Line> callBack) {
		var logm = new LogManager(callBack);
		var scriptContext = new ScriptContext(logm, dataSourceService, this, snmpService);
		var param = new HashMap<String, Object>();
		scriptContext.run(script, param);
	}

}
