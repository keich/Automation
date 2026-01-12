package ru.keich.mon.automation.httpdatasource.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.provider.DataProvider;

import ru.keich.mon.automation.httpdatasource.HttpDataSource;
import ru.keich.mon.automation.httpdatasource.HttpDataSourceService;

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

public class HttpDataSourceEdit extends Div {
	private static final long serialVersionUID = -1821458437205081304L;
	
	private static final double SPLIT_POS = 20;

	private final HttpDataSourceService httpDataSourceService;
	private final HttpDataSourceRight right;
	private final HttpDataSourceLeft left;

	public HttpDataSourceEdit(HttpDataSourceService httpDataSourceService) {
		super();
		
		this.setSizeFull();
		this.setHeightFull();
		
		this.httpDataSourceService = httpDataSourceService;
		
		this.right = new HttpDataSourceRight(this::save, this::delete);
		this.left = new HttpDataSourceLeft(DataProvider.fromCallbacks(httpDataSourceService::getAll, httpDataSourceService::getCount),
				right::open, right::add);

		var split = new SplitLayout(left, right);
		split.setSplitterPosition(SPLIT_POS);
		split.setSizeFull();
		split.setHeightFull();
		this.add(split);
	}

	private void save(HttpDataSource dataSource) {
		httpDataSourceService.save(dataSource);
		left.refresh();
	}

	private void delete(HttpDataSource dataSource) {
		httpDataSourceService.delete(dataSource);
		left.refresh();
	}
}
