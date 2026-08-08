package org.the4thlaw.commons.services.importing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.the4thlaw.common.dao.IDatabaseDao;
import org.the4thlaw.commons.exception.CommonErrorCode;
import org.the4thlaw.commons.exception.CommonException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class BaseXmlHandler<D extends IDatabaseDao, R extends BaseRelationsHolder> extends DefaultHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseXmlHandler.class);

	protected final D databaseDao;
	protected final R relations;

	public BaseXmlHandler(D databaseDao, R relations) {
		this.databaseDao = databaseDao;
		this.relations = relations;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes)
			throws SAXException {
		if ("version".equals(localName)) {
			handleVersion(attributes);
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		if ("library".equals(localName)) {
			persistRelations();
		}
	}

	protected void handleVersion(Attributes attributes) throws SAXException {
		int currentSchemaVersion = databaseDao.getSchemaVersion();
		Integer importSchemaVersion = null;
		for (int i = 0; i < attributes.getLength(); i++) {
			if ("schema".equals(attributes.getLocalName(i))) {
				importSchemaVersion = Integer.parseInt(attributes.getValue(i));
			}
		}
		if (importSchemaVersion == null || importSchemaVersion > currentSchemaVersion) {
			throw new SAXException(new CommonException(CommonErrorCode.IMPORT_WRONG_SCHEMA,
					"The imported file schema is at version " + importSchemaVersion
							+ " and seems to originate from an incompatible version of Demyo. "
							+ "This version of the application can only import schema versions " + currentSchemaVersion
							+ " and below."));
		}
	}

	protected void createLine(String tableName, Attributes attributes) {
		Map<String, String> attributeMap = toMap(attributes);
		createLine(tableName, attributeMap);
	}

	protected void createLine(String tableName, Map<String, ?> attributes) {
		databaseDao.insert(tableName, attributes);
	}

	protected static Map<String, String> toMap(Attributes attributes) {
		Map<String, String> attributeMap = new HashMap<>(attributes.getLength());
		for (int i = 0; i < attributes.getLength(); i++) {
			attributeMap.put(attributes.getLocalName(i), attributes.getValue(i));
		}
		return attributeMap;
	}

	protected void persistRelations() throws SAXException {
		LOGGER.debug("Persisting many-to-many relationships");
		for (Entry<String, List<Map<String, String>>> entry : relations.getAllRelations().entrySet()) {
			String tableName = entry.getKey();
			List<Map<String, String>> tableContent = entry.getValue();
			LOGGER.debug("{} entries for {}", tableContent.size(), tableName);
			for (Map<String, String> line : tableContent) {
				databaseDao.insert(tableName, line);
			}
		}
	}
}
