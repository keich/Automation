package ru.keich.mon.automation.script.version;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.keich.mon.automation.script.version.dataProviderDto.IScriptVersionLatestVersionQuery;
import java.util.Optional;

public interface ScriptVersionRepository extends JpaRepository<ScriptVersion, Integer> {
	public Optional<IScriptVersionLatestVersionQuery> findFirstByNameOrderByVersionDesc(String name);
}
