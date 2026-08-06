package org.the4thlaw.commons.services.exporting.impl;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipOutputStream;

import org.the4thlaw.commons.exception.CommonErrorCode;
import org.the4thlaw.commons.exception.CommonException;
import org.the4thlaw.commons.services.exporting.ExportOutput;
import org.the4thlaw.commons.services.exporting.IExportService;
import org.the4thlaw.commons.services.exporting.IExporter;
import org.the4thlaw.commons.services.io.IDirectoryService;
import org.the4thlaw.commons.utils.io.ZipUtils;

import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;

/**
 * Base implementation of an export services.
 * <p>
 * This class stores all data by respecting the contract of the provided {@link IDirectoryService}
 * </p>
 * 
 * @since 1.5
 */
@Validated
public abstract class BaseExportService implements IExportService {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseExportService.class);
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private final List<IExporter> exporters = new Vector<>();
	private final IDirectoryService directoryService;
	private final Path exportDirectory;

	public BaseExportService(IDirectoryService directoryService) {
		this.directoryService = directoryService;
		exportDirectory = directoryService.getExportDirectory();
	}

	public void registerExporter(@NotNull IExporter exporter) {
		LOGGER.debug("Registering exporter of type: {}", exporter.getClass().getCanonicalName());
		exporters.add(exporter);
	}

	public ExportOutput export(boolean withResources) throws CommonException {
		// Note that we don't check if withResources makes sense for the selected exporter.
		// It's not a issue at the moment.

		IExporter exporter = exporters.get(0);
		String baseExportFileName = "demyo_" + LocalDate.now().format(DATE_FORMAT) + ".";

		Path libraryExport = exporter.export();
		LOGGER.debug("Data export complete");

		if (!withResources) {
			return new ExportOutput(libraryExport, baseExportFileName + exporter.getExtension(false));
		}

		LOGGER.debug("Adding resources to export");
		// Build the ZIP file including all resources
		Path zipFile = directoryService.createTempFile("demyo-export-archive-",
				"." + exporter.getExtension(true), exportDirectory);

		try (OutputStream fos = Files.newOutputStream(zipFile);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				ZipOutputStream zos = new ZipOutputStream(bos)) {
			// The file inside the archive must always have the same name to be imported back
			ZipUtils.compress(libraryExport, "demyo." + exporter.getExtension(false), zos);
			ZipUtils.compress(directoryService.getImagesDirectory(), "images", zos);
		} catch (IOException | RuntimeException e) {
			LOGGER.warn("Failed to export", e);
			throw new CommonException(CommonErrorCode.EXPORT_IO_ERROR);
		}

		long length = -1;
		try {
			length = Files.size(zipFile);
		} catch (IOException e) {
			LOGGER.warn("Failed to get the file size", e);
		}
		LOGGER.debug("All resources added, export is fully complete ({} bytes)", length);

		return new ExportOutput(zipFile, baseExportFileName + exporter.getExtension(true));
	}
}
