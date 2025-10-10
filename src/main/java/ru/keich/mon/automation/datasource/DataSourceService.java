package ru.keich.mon.automation.datasource;

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
import ru.keich.mon.automation.ui.simpleEdit.SimpleEditService;

@Service
@Log
public class DataSourceService implements SimpleEditService<DataSource> {

	private final DataSourceRepository dataSourceRepository;

	private final Map<String, JdbcTemplate> jdbc = new ConcurrentHashMap<>();

	public DataSourceService(DataSourceRepository dataSourceRepository) {
		super();
		this.dataSourceRepository = dataSourceRepository;
	}

	@Override
	public Stream<DataSource> getAll(Query<DataSource, Void> q) {
		return dataSourceRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit());
	}

	@Override
	public int getCount(Query<DataSource, Void> q) {
		return Math.toIntExact(dataSourceRepository.findAll().stream().skip(q.getOffset()).limit(q.getLimit()).count());
	}

	@Override
	public void save(DataSource dataSource) {
		dataSourceRepository.save(dataSource);
	}

	@Override
	public void delete(DataSource dataSource) {
		dataSourceRepository.delete(dataSource);
	}

	public Optional<DataSource> get(String id) {
		return dataSourceRepository.findById(id);
	}

	private JdbcTemplate getJdbcTemplate(DataSource conf) {
		final HikariDataSource ds = new HikariDataSource();
		ds.setMaximumPoolSize(100);
		ds.setDriverClassName(conf.getDbClass());
		ds.setJdbcUrl(conf.getURL());
		;
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
			throw new DataSourceMissing();
		}
		return dataSource.queryForList(sql);
	}
}
