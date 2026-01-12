package ru.keich.mon.automation.script.ui;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;

import ru.keich.mon.automation.script.ScriptService;

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

public class ScriptDataProvider extends AbstractBackEndDataProvider<String, String> {

	private static final long serialVersionUID = 2728430003441011070L;

	private final ScriptService scriptService;

	public ScriptDataProvider(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	@Override
	protected Stream<String> fetchFromBackEnd(Query<String, String> query) {
		return scriptService.getAll(query);
	}

	@Override
	protected int sizeInBackEnd(Query<String, String> query) {
		return scriptService.getCount(query);
	}

}
