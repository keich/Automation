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

@Service
@Log
public class DBDataSourceService {

	private final DBDataSourceRepository dataSourceRepository;

	private final Map<String, JdbcTemplate> jdbc = new ConcurrentHashMap<>();

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
		jdbc.remove(dataSource.getName());
	}
	
	public void delete(DBDataSource dataSource) {
		dataSourceRepository.delete(dataSource);
		jdbc.remove(dataSource.getName());
	}

	public Optional<DBDataSource> get(String id) {
		return dataSourceRepository.findById(id);
	}

	private JdbcTemplate getJdbcTemplate(DBDataSource conf) {
		final HikariDataSource ds = new HikariDataSource();
		ds.setMaximumPoolSize(100);
		ds.setDriverClassName(conf.getDbClass());
		ds.setJdbcUrl(conf.getURL());
		ds.setUsername(conf.getLogin());
		ds.setPassword(conf.getPassword());
		return new JdbcTemplate(ds);
	}

	private JdbcTemplate getJdbcTemplate(String name) {
		return jdbc.compute(name, (key, ds) -> {
			if (ds == null) {
				try {
					ds = dataSourceRepository.findById(name).map(this::getJdbcTemplate).orElse(null);
				} catch (RuntimeException e) {
					log.severe(e.getMessage());
					e.printStackTrace();
				}
			}
			return ds;
		});
	}

	public List<Map<String, Object>> queryForList(String dataSourceName, String sql) {
		var dataSource = getJdbcTemplate(dataSourceName);
		if (dataSource == null) {
			throw new DBDataSourceMissing();
		}
		return dataSource.queryForList(sql);
	}
}
