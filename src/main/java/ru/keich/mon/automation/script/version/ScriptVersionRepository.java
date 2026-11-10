package ru.keich.mon.automation.script.version;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.keich.mon.automation.script.version.dataProviderDto.IScriptVersionIdQuery;
import ru.keich.mon.automation.script.version.dataProviderDto.IScriptVersionLatestVersionQuery;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.time.Instant;

public interface ScriptVersionRepository extends JpaRepository<ScriptVersion, Integer> {
	public Optional<IScriptVersionLatestVersionQuery> findFirstByNameOrderByVersionDesc(String name);
	public List<IScriptVersionIdQuery> findAllInstantByNameOrderById(String name, Pageable page);
	public List<ScriptVersion> findAllByNameOrderById(String name, Pageable page);
	public int countByName(String name);
	public Optional<ScriptVersion> findById(Instant id);
}