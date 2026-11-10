package ru.keich.mon.automation.script.version;

import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import ru.keich.mon.automation.script.version.dataProviderDto.IScriptVersionIdQuery;
import ru.keich.mon.automation.script.version.dataProviderDto.ScriptVersionSave;

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
						.map(i -> i.getVersion() + 1).orElse(1),
				svs.note(), svs.script().getName(), svs.script().getCode()));
	}

	public Stream<ScriptVersion> findAllByName(int pSize, int page, String name) {
		return _scriptVersionRepo
				.findAllByNameOrderById(name, PageRequest.of(page, pSize)).stream();
	}
	public Stream<Instant> findAllInstantByName(int pSize, int page, String name) {
		return _scriptVersionRepo
				.findAllInstantByNameOrderById(name, PageRequest.of(page, pSize)).stream()
				.map(IScriptVersionIdQuery::id);
	}

	public int getAllByNameCount(String name) {
		return _scriptVersionRepo.countByName(name);
	}

	public Optional<ScriptVersion> findById(Instant id) {
		return _scriptVersionRepo.findById(id);
	}

}
