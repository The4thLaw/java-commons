package org.the4thlaw.commons.services.exporting;

import org.the4thlaw.commons.exception.CommonException;

/**
 * This service allows exporting app data to various formats.
 * <p>
 * Exporters need to be registered.
 * </p>
 * 
 * @since 1.5
 */
public interface IExportService {

	/**
	 * Registers an exporter to the service.
	 *
	 * @param exporter The exporter to register.
	 */
	void registerExporter(IExporter exporter);

	/**
	 * Exports the library.
	 *
	 * @param withResources Whether to include resources in this export.
	 * @return The export output.
	 * @throws CommonException In case of error during export.
	 */
	ExportOutput export(boolean withResources) throws CommonException;
}

