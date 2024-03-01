package org.the4thlaw.commons.utils.h2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.h2.engine.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An opiniated H2 version and migration manager.
 * <p>Relies on a file named {@value #VERSION_FILE_NAME} to identify the version.</p>
 */
public class H2VersionManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(H2VersionManager.class);
	/** The name of the file containing the H2 version */
	/*default*/ static final String VERSION_FILE_NAME = "t4l-h2-version.txt";

	private final int defaultVersion;
	private final Path databaseDirectory;
	private final H2LocalUpgrader upgrader;

	/**
	 * Creates a versions manager that does not tolerate missing database version files (checked at migration time).
	 * @param databaseDirectory The directory containing the H2 database files.
	 * @param upgrader The version upgrader.
	 */
	public H2VersionManager(Path databaseDirectory, H2LocalUpgrader upgrader) {
		this(-1, databaseDirectory, upgrader);
	}

	/**
	 * Creates a versions manager.
	 * @param defaultVersion The default version of the database, if the version file doesn't exist.
	 * @param databaseDirectory The directory containing the H2 database files.
	 * @param upgrader The version upgrader.
	 */
	public H2VersionManager(int defaultVersion, Path databaseDirectory, H2LocalUpgrader upgrader) {
		this.defaultVersion = defaultVersion;
		this.databaseDirectory = databaseDirectory;
		this.upgrader = upgrader;
	}

	private Path getVersionFile() {
		return databaseDirectory.resolve(VERSION_FILE_NAME);
	}

	/**
	 * Gets the current version of the database.
	 * @return The declared or default version (if a default version is allowed).
	 */
	public int getCurrentVersion() {
		Path databaseVersionFile = getVersionFile();

		int version;
		if (!Files.exists(databaseVersionFile)) {
			if (defaultVersion < 0) {
				throw new H2MigrationException(
						"The H2 versioning file is missing and no default version has been configured");
			}
			version = defaultVersion;
		} else {
			String versionStr;
			try {
				versionStr = new String(Files.readAllBytes(databaseVersionFile), StandardCharsets.UTF_8);
			} catch (IOException e) {
				throw new H2MigrationException("Failed to read the database version file at " + databaseVersionFile, e);
			}
			version = Integer.parseInt(versionStr);
		}
		return version;
	}

	/**
	 * Migrates the database provided in URL if needed.
	 * @param isNewDatabase {@code true} if we're dealing with a fresh database (will just write the version file).
	 * @param url The URL to connect to the database.
	 * @param dbUser The database user.
	 * @param dbPassword The database password.
	 */
	public void migrateH2IfNeeded(boolean isNewDatabase, String url, String dbUser, String dbPassword) {
		Path databaseVersionFile = getVersionFile();
		if (!isNewDatabase) {
			LOGGER.debug("Checking if the database's H2 version must be migrated");
			int version = getCurrentVersion();

			if (version != Constants.BUILD_ID) {
				LOGGER.info("Migrating the H2 database from {} to {}", version, Constants.BUILD_ID);
				Properties dbMigProps = new Properties();
				dbMigProps.setProperty("user", dbUser);
				dbMigProps.setProperty("password", dbPassword);
				upgrader.upgrade(url, dbMigProps, version);
			} else {
				LOGGER.debug("The H2 database is at version {}, same as what the current H2 installation requires",
						version);
			}
		}

		// Always write the current H2 version, as long as the migration succeeded
		try {
			Files.write(databaseVersionFile, String.valueOf(Constants.BUILD_ID).getBytes(StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new H2MigrationException("Failed to write the database version file at " + databaseVersionFile, e);
		}
	}
}
