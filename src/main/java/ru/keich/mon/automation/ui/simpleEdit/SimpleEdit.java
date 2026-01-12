package ru.keich.mon.automation.ui.simpleEdit;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.function.ValueProvider;

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

public class SimpleEdit<T extends SimpleEditItem> extends Div {

	private static final long serialVersionUID = 2851527096870738150L;
	private static final double SPLIT_POS = 20;

	private final SimpleEditRight right;
	private final SimpleEditLeft<T> left;
	private final SimpleEditService<T> service;

	private final Class<T> clazz;

	public SimpleEdit(SimpleEditService<T> service, Class<T> clazz) {
		this.service = service;
		this.clazz = clazz;

		this.setSizeFull();
		this.setHeightFull();

		right = new SimpleEditRight(this::save, this::delete, this::validate);

		try {
			T ac = clazz.getDeclaredConstructor().newInstance();
			right.updateFormFields(ac.getFormFields());
		} catch (Exception e) {
			e.printStackTrace();
		}

		left = new SimpleEditLeft<T>(DataProvider.fromCallbacks(service::getAll, service::getCount), this::leftClick,
				right::addNew);

		var split = new SplitLayout(left, right);
		split.setSplitterPosition(SPLIT_POS);
		split.setSizeFull();
		split.setHeightFull();
		this.add(split);

	}

	public void addColumn(ValueProvider<T, ?> valueProvider) {
		left.addColumn(valueProvider);
	}

	private void save(String group, Map<String, String> data) {
		try {
			T ac = clazz.getDeclaredConstructor().newInstance();
			ac.fromMap(group, data);
			if (ac.validate()) {
				service.save(ac);
				left.refresh();
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	private void delete(String group, Map<String, String> data) {
		try {
			T ac = clazz.getDeclaredConstructor().newInstance();
			ac.fromMap(group, data);
			service.delete(ac);
			right.addNew();
			left.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean validate(String group, Map<String, String> data) {
		try {
			T ac = clazz.getDeclaredConstructor().newInstance();
			ac.fromMap(group, data);
			return ac.validate();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void leftClick(T ds) {
		right.open(ds.getGroup(), ds.toMap());
	}

}
