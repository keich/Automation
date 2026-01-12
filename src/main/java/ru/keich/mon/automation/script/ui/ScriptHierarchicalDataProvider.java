package ru.keich.mon.automation.script.ui;

import java.util.Optional;
import java.util.stream.Stream;

import com.vaadin.flow.data.provider.hierarchy.AbstractBackEndHierarchicalDataProvider;
import com.vaadin.flow.data.provider.hierarchy.HierarchicalQuery;

import ru.keich.mon.automation.script.Script;
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

public class ScriptHierarchicalDataProvider extends AbstractBackEndHierarchicalDataProvider<Script, Void> {

	private static final long serialVersionUID = -890378091045461883L;

	private final ScriptService scriptService;

	public ScriptHierarchicalDataProvider(ScriptService scriptService) {
		this.scriptService = scriptService;
	}

	@Override
	public int getChildCount(HierarchicalQuery<Script, Void> query) {
		return scriptService.getCountOrRoot(query);
	}

	@Override
	public boolean hasChildren(Script item) {
		return scriptService.getChildCountOrRoot(Optional.ofNullable(item.getName())) > 0;
	}

	@Override
	protected Stream<Script> fetchChildrenFromBackEnd(HierarchicalQuery<Script, Void> query) {
		return scriptService.getAllOrRoot(query);
	}

}
