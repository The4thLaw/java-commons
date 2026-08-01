package org.the4thlaw.commons.services.exporting;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.the4thlaw.common.dao.IDatabaseDao;
import org.the4thlaw.commons.exception.CommonErrorCode;
import org.the4thlaw.commons.exception.CommonException;
import org.the4thlaw.commons.services.io.IDirectoryService;
import org.the4thlaw.commons.utils.xml.XMLUtils;

import javanet.staxutils.IndentingXMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseXmlExporter implements IExporter {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseXmlExporter.class);

	private static final ThreadLocal<DateFormat> DATE_FORMAT = ThreadLocal
			.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));

	private final String appName;
	private final String appVersion;
	private final String zipExtensionName;
	protected final IDirectoryService directoryService;
	protected final IDatabaseDao databaseDao;

	protected BaseXmlExporter(String appName, String appVersion, String zipExtensionName,
			IDirectoryService directoryService, IDatabaseDao databaseDao) {
		this.appName = appName;
		this.appVersion = appVersion;
		this.zipExtensionName = zipExtensionName;
		this.directoryService = directoryService;
		this.databaseDao = databaseDao;
	}

	@Override
	public Path export() throws CommonException {
		LOGGER.debug("Starting export in XML format");

		Path out = directoryService.createTempFile(appName + "-export-", ".xml");

		XMLStreamWriter xsw = null;
		try (OutputStream outputStream = Files.newOutputStream(out)) {

			xsw = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, StandardCharsets.UTF_8.toString());
			xsw = new IndentingXMLStreamWriter(xsw);

			xsw.writeStartDocument();
			xsw.writeStartElement("library");
			writeMeta(xsw);
			exportModels(xsw);
			xsw.writeEndElement();
			xsw.writeEndDocument();

			xsw.close();
		} catch (IOException e) {
			throw new CommonException(CommonErrorCode.EXPORT_IO_ERROR, e);
		} catch (XMLStreamException e) {
			throw new CommonException(CommonErrorCode.EXPORT_XML_ERROR, e);
		} finally {
			// We will most likely not need this anymore in the current request. Clear it to avoid leaks.
			DATE_FORMAT.remove();

			XMLUtils.closeQuietly(xsw);
		}

		return out;
	}

	protected abstract void exportModels(XMLStreamWriter xsw) throws XMLStreamException;

	protected void exportModel(XMLStreamWriter xsw, String listTag, String entityTag, String tableName,
			ManyToManyRelation... relations) throws XMLStreamException {
		List<Map<String, Object>> records = databaseDao.getRawRecords(tableName);

		if (records.isEmpty()) {
			return;
		}

		xsw.writeStartElement(listTag);

		for (Map<String, Object> rec : records) {
			Number recordId = (Number) rec.get("ID"); // By convention
			boolean hasRelations = hasRelations(relations, recordId);
			if (hasRelations) {
				xsw.writeStartElement(entityTag);
			} else {
				xsw.writeEmptyElement(entityTag);
			}

			// Write entity fields
			for (Entry<String, Object> field : rec.entrySet()) {
				if (field.getValue() != null) {
					xsw.writeAttribute(field.getKey().toLowerCase(), toString(field.getValue()));
				}
			}

			// Write relations
			for (ManyToManyRelation rel : relations) {
				rel.writeRelationToStream(recordId, xsw);
			}

			if (hasRelations) {
				xsw.writeEndElement();
			}
		}

		xsw.writeEndElement();
	}

	private void writeMeta(XMLStreamWriter xsw) throws XMLStreamException {
		xsw.writeStartElement("meta");

		xsw.writeEmptyElement("version");
		xsw.writeAttribute(appName, appVersion);
		xsw.writeAttribute("schema", String.valueOf(databaseDao.getSchemaVersion()));

		xsw.writeEmptyElement("counts");

		for (Entry<String, Long> table : databaseDao.getEntityTableCounts().entrySet()) {
			xsw.writeAttribute(table.getKey(), Long.toString(table.getValue()));
		}

		xsw.writeEndElement();
	}

	protected static boolean hasRelations(ManyToManyRelation[] relations, Number recordId) {
		for (ManyToManyRelation rel : relations) {
			if (rel.hasRelations(recordId)) {
				return true;
			}
		}
		return false;
	}

	protected static String toString(Object value) {
		if (value == null) {
			return "";
		}
		if (value instanceof Date asDate) {
			return DATE_FORMAT.get().format(asDate);
		}
		return value.toString();
	}

	@Override
	public String getExtension(boolean withResources) {
		return withResources ? zipExtensionName : "xml";
	}
}
