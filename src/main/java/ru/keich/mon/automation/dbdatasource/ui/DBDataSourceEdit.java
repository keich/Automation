package ru.keich.mon.automation.dbdatasource.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.provider.DataProvider;

import ru.keich.mon.automation.dbdatasource.DBDataSource;
import ru.keich.mon.automation.dbdatasource.DBDataSourceService;

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

public class DBDataSourceEdit extends Div {

	private static final long serialVersionUID = -1821458437205081304L;
	
	private static final double SPLIT_POS = 20;

	private final DBDataSourceService dbDataSourceService;
	private final DBDataSourceRight right;
	private final DBDataSourceLeft left;

	public DBDataSourceEdit(DBDataSourceService dbDataSourceService) {
		super();
		
		this.setSizeFull();
		this.setHeightFull();
		
		this.dbDataSourceService = dbDataSourceService;
		
		this.right = new DBDataSourceRight(this::save, this::delete);
		this.left = new DBDataSourceLeft(DataProvider.fromCallbacks(dbDataSourceService::getAll, dbDataSourceService::getCount),
				right::open, right::add);

		var split = new SplitLayout(left, right);
		split.setSplitterPosition(SPLIT_POS);
		split.setSizeFull();
		split.setHeightFull();
		this.add(split);
	}

	private void save(DBDataSource dbDataSource) {
		dbDataSourceService.save(dbDataSource);
		left.refresh();
	}

	private void delete(DBDataSource dbDataSource) {
		dbDataSourceService.delete(dbDataSource);
		left.refresh();
	}

}
