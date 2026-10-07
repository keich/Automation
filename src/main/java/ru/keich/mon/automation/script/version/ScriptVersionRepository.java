package ru.keich.mon.automation.script.version;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.keich.mon.automation.script.version.dataProviderDto.IScriptVersionLatestVersionQuery;
import java.util.List;
import java.util.Optional;

public interface ScriptVersionRepository extends JpaRepository<ScriptVersion, Integer> {
	public Optional<List<Integer>> findByNameOrderByVersion(String name, Pageable page);
	
	public Integer countByName(String name);
	
	public Optional<IScriptVersionLatestVersionQuery> findFirstByNameOrderByVersion(String name);
}
