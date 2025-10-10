package ru.keich.mon.automation.script;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScriptRepository extends JpaRepository<Script, String> {

	public List<Script> findByNameContainingIgnoreCase(String name);

	@Query("select s from Script s WHERE s.name <> s.parent AND s.parent = ?1")
	public List<Script> findByParent(String parent);

	@Query("select s from Script s WHERE s.name = s.parent OR s.parent is null")
	public List<Script> findRoot();

}
