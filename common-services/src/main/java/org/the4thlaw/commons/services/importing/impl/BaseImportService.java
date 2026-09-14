package org.the4thlaw.commons.services.importing.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import org.the4thlaw.common.dao.IDatabaseDao;
import org.the4thlaw.commons.exception.CommonErrorCode;
import org.the4thlaw.commons.exception.CommonException;
import org.the4thlaw.commons.services.importing.IImportService;
import org.the4thlaw.commons.services.importing.IImporter;
import org.the4thlaw.commons.services.io.IDirectoryService;
import org.the4thlaw.commons.utils.io.FileUtils;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;

/**
 * Base implementation of an import service.
 * <p>
 * This class stores all data by respecting the contract of the provided {@link IDirectoryService}
 * </p>
 * 
 * @since 1.5
 */
@Validated 
public abstract class BaseImportService<D extends IDatabaseDao> implements IImportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseImportService.class);

	private final List<IImporter> importers = new Vector<>();
    private final String appName;
    private final IDirectoryService directoryService;
	private final D databaseDao;

    public BaseImportService(String appName, IDirectoryService directoryService, D databaseDao) {
        this.appName = appName.toLowerCase(Locale.ROOT);
        this.directoryService = directoryService;
		this.databaseDao = databaseDao;
	}

	@Override
	public void registerImporter(@NotNull IImporter importer) {
		LOGGER.debug("Registering importer of type: {}", importer.getClass().getCanonicalName());
		importers.add(importer);
	}

	@Override
	public void importFile(String originalFilename, InputStream content) throws CommonException {
		Path importFile = null;
		OutputStream fos = null;
		BufferedOutputStream bos = null;
		try {
			// Copy the file to some place we know, so that importers can peek
            importFile = directoryService.createTempFile(appName + "-import", ".tmp");
			fos = Files.newOutputStream(importFile);
			bos = new BufferedOutputStream(fos);
			content.transferTo(bos);
			org.the4thlaw.commons.utils.io.IOUtils.closeQuietly(bos);

			// Detect importer to use
			IImporter importer = null;
			for (IImporter imp : importers) {
				if (imp.supports(originalFilename, importFile)) {
					importer = imp;
					LOGGER.debug("Found a supporting importer of type: {}", importer.getClass().getCanonicalName());
					break;
				}
			}
			if (importer == null) {
				throw new CommonException(CommonErrorCode.IMPORT_FORMAT_NOT_SUPPORTED);
			}

			// Clear the database
			databaseDao.pruneAllTables();

			// Perform actual import
			importer.importFile(originalFilename, importFile);
		} catch (IOException e) {
			throw new CommonException(CommonErrorCode.IMPORT_IO_ERROR, e);
		} finally {
			FileUtils.deleteQuietly(importFile);
			org.the4thlaw.commons.utils.io.IOUtils.closeQuietly(content);
			org.the4thlaw.commons.utils.io.IOUtils.closeQuietly(bos);
			org.the4thlaw.commons.utils.io.IOUtils.closeQuietly(fos);
		}
	}
}
