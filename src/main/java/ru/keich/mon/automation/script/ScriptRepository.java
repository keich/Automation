package ru.keich.mon.automation.script;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

public interface ScriptRepository extends JpaRepository<Script, String> {

	public List<Script> findByNameContainingIgnoreCase(String name);

	@Query("select s from Script s WHERE s.name <> s.parent AND s.parent = ?1")
	public List<Script> findByParent(String parent);

	@Query("""		
			select s from Script s 
			LEFT JOIN Script p ON p.name = s.parent
			WHERE s.name = s.parent OR p.name is null
			""")
	public List<Script> findRoot();

}
