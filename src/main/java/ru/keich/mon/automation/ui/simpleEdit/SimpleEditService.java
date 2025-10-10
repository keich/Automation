package ru.keich.mon.automation.ui.simpleEdit;

import java.util.stream.Stream;

import com.vaadin.flow.data.provider.Query;

public interface SimpleEditService<T> {

	public Stream<T> getAll(Query<T, Void> q);

	public int getCount(Query<T, Void> q);

	public void save(T dataSource);

	public void delete(T dataSource);

}
