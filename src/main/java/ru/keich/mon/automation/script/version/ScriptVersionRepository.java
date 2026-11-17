package ru.keich.mon.automation.script.version;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.keich.mon.automation.script.version.dataProviderDto.IScriptVersionIdQuery;
import ru.keich.mon.automation.script.version.dataProviderDto.IScriptVersionLatestVersionQuery;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.time.Instant;

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

public interface ScriptVersionRepository extends JpaRepository<ScriptVersion, Integer> {
	public Optional<IScriptVersionLatestVersionQuery> findFirstByNameOrderByVersionDesc(String name);
	public List<IScriptVersionIdQuery> findAllInstantByNameOrderById(String name, Pageable page);
	public List<ScriptVersion> findAllByNameOrderById(String name, Pageable page);
	public int countByName(String name);
	public Optional<ScriptVersion> findById(Instant id);
}