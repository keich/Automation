package ru.keich.mon.automation.script.version;

import java.util.Optional;
import java.util.stream.Stream;
import java.time.Instant;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.vaadin.flow.data.provider.Query;

import ru.keich.mon.automation.script.version.dataProviderDto.*;
import java.util.List;

@Service
public class ScriptVersionService {
	private final ScriptVersionRepository _scriptVersionRepo;

	public ScriptVersionService(ScriptVersionRepository scriptVersionRepo) {
		this._scriptVersionRepo = scriptVersionRepo;
	}

	@Transactional(isolation = Isolation.REPEATABLE_READ)
	public ScriptVersion save(ScriptVersionSave svs) {
		return this._scriptVersionRepo.save(new ScriptVersion(
				this._scriptVersionRepo.findFirstByNameOrderByVersionDesc(svs.script().getName())
						.map(i -> i.getVersion()+1).orElse(1),
				svs.note(), svs.script().getName(), svs.script().getCode()));
	}
	
	public Stream<Instant> findAllByName(int pSize, int page, String name) {
		return _scriptVersionRepo.findAllByNameOrderById(name, PageRequest.of(page, pSize)).stream().map(IScriptVersionIdQuery::id);
	}
	public int getAllByNameCount(String name) {
		return _scriptVersionRepo.countByName(name);
	}
	public ScriptVersion findById(Instant id) {
		return _scriptVersionRepo.findById(id).orElseThrow();
	}
}
