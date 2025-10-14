package ru.keich.mon.automation.script;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import lombok.extern.java.Log;
import ru.keich.mon.automation.datasource.DataSourceService;
import ru.keich.mon.automation.scripting.LogManager;
import ru.keich.mon.automation.scripting.LogManager.Line;
import ru.keich.mon.automation.scripting.Root;

@Service
@Log
public class ScriptService {

	public static final String LANG_JS = "js";

	public static final String LOG_MSG_HIERAR_CIRCLE = "Circle found";
	public static final String LOG_MSG_RUN_OK = "Running with result: ";
	public static final String LOG_MSG_RUN_ERR = "Running with error: ";

	private final ScriptRepository scriptRepository;
	private final DataSourceService dataSourceService;

	public ScriptService(ScriptRepository scriptRepository, DataSourceService dataSourceService) {
		this.scriptRepository = scriptRepository;
		this.dataSourceService = dataSourceService;
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

	private Context cretaeContext(LogManager logm) {
		var context = Context.newBuilder(LANG_JS).allowHostAccess(HostAccess.ALL)
				.allowHostClassLookup(className -> true).build();
		var bindings = context.getBindings(LANG_JS);
		Root.getMembers(logm, dataSourceService).entrySet().forEach(e -> {
			bindings.putMember(e.getKey(), e.getValue());
		});
		return context;
	}

	public void run(String name) {
		scriptRepository.findById(name)
				.ifPresent(script -> {
					run(script, l -> {});
				});
	}
	
	public void run(Script script, Consumer<Line> callBack) {
		var logm = new LogManager(callBack);
		try {
			var context = cretaeContext(logm);
			var history = new HashSet<String>();
			var result = runHierarchical(logm, script, context, history);
			if (result.get(ScriptResult.KEY_RESULT) != null) {
				logm.info(LOG_MSG_RUN_OK + result.get(ScriptResult.KEY_RESULT));
			} else {
				logm.severe(LOG_MSG_RUN_ERR + result.get(ScriptResult.KEY_ERR));
			}
		} catch (Exception e) {
			logm.severe(e.getMessage());
			// e.printStackTrace();
		}
	}

	private Map<String, Object> runHierarchical(LogManager logm, Script script, Context context, Set<String> history) {
		var scriptName = script.getName();
		if (history.contains(scriptName)) {
			throw new ScriptCycleException(LOG_MSG_HIERAR_CIRCLE);
		}
		var result = new HashMap<String, Map<String, Object>>();
		history.add(scriptName);
		var children = getChild(scriptName);
		children.stream().forEach(child -> {
			try {
				result.put(child.getName(), runHierarchical(logm, child, context, history));
			} catch (Exception e) {
				result.put(child.getName(), ScriptResult.err(e.getMessage()));
				logm.severe(e.getMessage());
				// e.printStackTrace();
			}
		});
		history.remove(scriptName);
		try {
			var func = context.eval(LANG_JS, script.getCode());
			return ScriptResult.ok(func.execute(result));
		} catch (Exception e) {
			logm.severe(e.getMessage());
			// e.printStackTrace();
			return ScriptResult.err(e.getMessage());
		}
	}

}
