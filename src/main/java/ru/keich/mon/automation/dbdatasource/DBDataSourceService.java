package ru.keich.mon.automation.dbdatasource;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.vaadin.flow.data.provider.Query;
import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.java.Log;

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
@Log
public class DBDataSourceService {

	private final DBDataSourceRepository dataSourceRepository;

	private final Map<String, JdbcTemplate> cache = new ConcurrentHashMap<>();

	public DBDataSourceService(DBDataSourceRepository dataSourceRepository) {
		super();
		this.dataSourceRepository = dataSourceRepository;
	}
	
	public Stream<DBDataSource> getAll(Query<DBDataSource, Void> q) {
		return dataSourceRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit());
	}
	
	public int getCount(Query<DBDataSource, Void> q) {
		return Math.toIntExact(dataSourceRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit()).count());
	}
	
	public void save(DBDataSource dataSource) {
		dataSourceRepository.save(dataSource);
		closeDataSource(dataSource.getName());
	}
	
	public void delete(DBDataSource dataSource) {
		dataSourceRepository.delete(dataSource);
		closeDataSource(dataSource.getName());
	}

	public Optional<DBDataSource> get(String id) {
		return dataSourceRepository.findById(id);
	}

	private JdbcTemplate getJdbcTemplate(DBDataSource conf) {
		final HikariDataSource ds = new HikariDataSource();
		ds.setMaximumPoolSize(conf.getMaximumPoolSize());
		ds.setDriverClassName(conf.getDbClass());
		ds.setJdbcUrl(conf.getURL());
		ds.setUsername(conf.getLogin());
		ds.setPassword(conf.getPassword());
		ds.setAutoCommit(true);
		return new JdbcTemplate(ds);
	}

	private JdbcTemplate getJdbcTemplate(String name) {
		return dataSourceRepository
				.findById(name)
				.map(this::getJdbcTemplate)
				.orElse(null);
	}
	
	private void closeDataSource(String dataSourceName) {
		cache.compute(dataSourceName, (key, ds) -> {
			if (ds != null) {
				try {
					var dataSource = (HikariDataSource)ds.getDataSource();
					dataSource.close();
				} catch (RuntimeException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
				}
			}
			return null;
		});
	}
	
	private JdbcTemplate getDataSource(String dataSourceName) {
		var dataSource = cache.compute(dataSourceName, (key, ds) -> {
			if (ds == null) {
				try {
					ds = getJdbcTemplate(dataSourceName);
				} catch (RuntimeException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
				}
			}
			return ds;
		});
		if (dataSource == null) {
			throw new DBDataSourceMissing();
		}
		return dataSource;
	}

	public List<Map<String, Object>> queryForList(String dataSourceName, String sql) {
		return getDataSource(dataSourceName).queryForList(sql);
	}
	
	public int update(String dataSourceName, String sql, Object[] args) {
		return getDataSource(dataSourceName).update(sql, args);
	}
	
}
