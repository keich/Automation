package ru.keich.mon.automation.script.version;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.flow.data.provider.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.Map;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedList;
import ru.keich.mon.automation.script.version.dataProviderDto.*;
import ru.keich.mon.automation.script.Script;

@Service
public class ScriptVersionService {
	private final ScriptVersionRepository _scriptVersionRepo;
	private final Function<Query<?,?>, Pageable> getPageableFromQuery;
	
	public ScriptVersionService(ScriptVersionRepository scriptVersionRepo) {
		this._scriptVersionRepo = scriptVersionRepo;
		this.getPageableFromQuery = (q)->{
			return Pageable.ofSize(q.getPageSize()).withPage(q.getPage());
		};
	}
	
	public Optional<List<Integer>> findByNameOrderByVersion(Query<Integer, ScriptVersionQuery> q) {	
		return this._scriptVersionRepo.findByNameOrderByVersion(q.getFilter().get().name(), 
				this.getPageableFromQuery.apply(q));
	}
	public Integer countByName(Query<Integer, ScriptVersionQuery> q) {
		return this._scriptVersionRepo.countByName(q.getFilter().get().name());
	}
	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public ScriptVersion save(ScriptVersion ver) {
		ver.setVersion(this.getLatestVersionIncrement(ver.getName()));
		return this._scriptVersionRepo.save(ver);
	}
	public ScriptVersion save(ScriptVersionSave svs) {
		return this.save(ScriptVersion.fromScript(svs));
	}
	public Integer getLatestVersionIncrement(String scriptName) {
		int version = 1;
		try {
			version = this._scriptVersionRepo.findFirstByNameOrderByVersion(scriptName).get().getVersion()+1;
		} catch(Exception e) {}
		return version;
	}
}
