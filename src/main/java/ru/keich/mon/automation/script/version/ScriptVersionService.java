package ru.keich.mon.automation.script.version;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.keich.mon.automation.script.version.dataProviderDto.*;

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
}
