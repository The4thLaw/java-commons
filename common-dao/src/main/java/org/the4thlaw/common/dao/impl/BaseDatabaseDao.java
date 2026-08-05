package org.the4thlaw.common.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.the4thlaw.common.dao.IDatabaseDao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Base implementation for IDatabaseDao
 */
public abstract class BaseDatabaseDao implements IDatabaseDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseDatabaseDao.class);

	private final String[] entityTables;
	private final List<String> allTables;
	private final EntityManager entityManager;
	private final JdbcTemplate jdbcTemplate;

	protected BaseDatabaseDao(String schemaVersionTable, String[] entityTables, String[] nonEntityTables,
			EntityManager entityManager, DataSource dataSource) {
		this.entityTables = entityTables;
		this.entityManager = entityManager;

		jdbcTemplate = new JdbcTemplate();
		jdbcTemplate.setDataSource(dataSource);

		allTables = new ArrayList<>(entityTables.length + nonEntityTables.length);
		Collections.addAll(allTables, nonEntityTables);
		Collections.addAll(allTables, entityTables);
	}

	private void executeUpdate(String sql) {
		Query query = entityManager.createNativeQuery(sql);
		query.executeUpdate();
	}

	@Override
	public void pruneAllTables() {
		LOGGER.debug("Pruning all tables: {}", allTables);
		for (String table : allTables) {
			executeUpdate("DELETE FROM " + table);
		}
	}

	@Override
	public void fixAutoIncrements() {
		LOGGER.debug("Fixing auto-increments");
		for (String table : entityTables) {
			executeUpdate(
					"ALTER TABLE " + table + " ALTER COLUMN ID RESTART WITH (SELECT MAX(id) + 1 FROM " + table + ")");
		}
	}

	@Override
	public void insert(String tableName, Map<String, ? extends Object> values) {
		StringBuilder insertSb = new StringBuilder("INSERT INTO ").append(tableName).append('(');
		StringBuilder valuesSb = new StringBuilder(" VALUES (");

		for (String col : values.keySet()) {
			insertSb.append(col).append(',');
			valuesSb.append(':').append(col).append(',');
		}
		insertSb.deleteCharAt(insertSb.length() - 1); // Remove last comma
		valuesSb.deleteCharAt(valuesSb.length() - 1); // Remove last comma
		insertSb.append(')');
		valuesSb.append(')');
		insertSb.append(valuesSb);
		Query query = entityManager.createNativeQuery(insertSb.toString());
		for (Entry<String, ? extends Object> entry : values.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		query.executeUpdate();
	}

	@Override
	public Map<String, Long> getEntityTableCounts() {
		return Stream.of(entityTables)
				.collect(Collectors.toMap(Function.identity(), t -> count(t)));
	}

	@Override
	public long count(String tableName) {
		Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM " + tableName);
		return ((Number) query.getSingleResult()).longValue();
	}

	@Override
	public List<Map<String, Object>> getRawRecords(String tableName) {
		return jdbcTemplate.queryForList("SELECT * FROM " + tableName);
	}

	@Override
	public int getSchemaVersion() {
		Integer version = jdbcTemplate.queryForObject(
				"select \"version\" from \"schema_version\" order by \"installed_rank\" desc LIMIT 1", Integer.class);
		if (version == null) {
			throw new PersistenceException("Failed to get the schema version");
		}
		return version;
	}
}
