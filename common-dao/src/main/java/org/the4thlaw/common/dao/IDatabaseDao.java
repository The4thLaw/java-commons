package org.the4thlaw.common.dao;

import java.util.List;
import java.util.Map;

import org.springframework.data.repository.NoRepositoryBean;

/**
 * Provides access to raw SQL operations, for cases when flexibility and reflection primes over clean Hibernate beans.
 */
@NoRepositoryBean
public interface IDatabaseDao {
	/**
	 * Deletes content from all tables in the application scope.
	 */
	void pruneAllTables();

	/**
	 * Fix the values of the identity columns, for example after an import or migration.
	 * <p>
	 * Due to a change in H2 2.x, the behaviour of identity columns has changed (see
	 * https://github.com/h2database/h2database/issues/3454). As such, we need to manually fix the values of the
	 * identity columns when we assign values of our own.
	 * </p>
	 */
	void fixAutoIncrements();

	/**
	 * Inserts arbitrary data into a table.
	 *
	 * @param tableName The table into which to insert the data.
	 * @param values A map of column-to-value data to insert.
	 */
	void insert(String tableName, Map<String, ? extends Object> values);

	/**
	 * Gets the row counts for all entity tables.
	 * @return
	 */
	Map<String, Long> getEntityTableCounts();

	/**
	 * Counts the number of entries in a table.
	 *
	 * @param tableName The table to retrieve the number of entries from.
	 * @return The number of entries
	 */
	long count(String tableName);

	/**
	 * Gets the raw data from a table.
	 *
	 * <p>Warning: the table and column names are sensitive to SQL injection.</p>
	 *
	 * @param tableName The table to query.
	 * @return The data contained inside the table.
	 */
	List<Map<String, Object>> getRawRecords(String tableName);

	/**
	 * Gets the version of the application schema.
	 *
	 * @return The schema version.
	 */
	int getSchemaVersion();
}
