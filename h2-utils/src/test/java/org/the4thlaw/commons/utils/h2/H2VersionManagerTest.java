package org.the4thlaw.commons.utils.h2;

import java.io.File;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Tests for {@link H2VersionManager}.
 */
class H2VersionManagerTest {
    private static final String DB_USER = "user";
    private static final String DB_PASSWORD = "password";
    private H2LocalUpgrader upgrader = new H2LocalUpgrader(Path.of("target/legacy-h2-versions"));

    /**
     * Tests various migrations to make sure everything works as excpected.
     * @param version The version of the database to migrate.
     * @throws SQLException In case of issue while checking the results.
     */
    @ParameterizedTest
    @ValueSource(strings =
    { "1.3.168", "1.4.196", "2.1.214" })
    void testMigration(String version) throws SQLException {
        int versionNumber = Integer.parseInt(version.substring(version.lastIndexOf(".") + 1));
        Path dbPath = Path.of("target/test-classes/sample-databases/v" + version);
        H2VersionManager manager = new H2VersionManager(versionNumber, dbPath, upgrader);

        String url = "jdbc:h2:" + dbPath.toAbsolutePath().toString() + File.separator + "sample";
        manager.migrateH2IfNeeded(false, url, "user", "password");

        // Use the default driver, which should be the version we migrated to
        JdbcDataSource ds = new JdbcDataSource();
		ds.setURL(url);
		ds.setUser(DB_USER);
		ds.setPassword(DB_PASSWORD);

        try (Connection conn = ds.getConnection()) {
            // Check group count
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM groups");
            ResultSet result = stmt.executeQuery()) {
                result.next();
                int count = result.getInt(1);
                assertThat(count).isEqualTo(2);
            }

             // Check user count
             try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users");
             ResultSet result = stmt.executeQuery()) {
                 result.next();
                 int count = result.getInt(1);
                 assertThat(count).isEqualTo(2);
             }

             // Check that we can insert a group and the auto_increment still works
             try (PreparedStatement stmt = conn.prepareStatement("INSERT INTO groups(name) VALUES ('group3')")) {
                 stmt.execute();
             }

             // Check group count again
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM groups");
            ResultSet result = stmt.executeQuery()) {
                result.next();
                int count = result.getInt(1);
                assertThat(count).isEqualTo(3);
            }
        }
    }
}
