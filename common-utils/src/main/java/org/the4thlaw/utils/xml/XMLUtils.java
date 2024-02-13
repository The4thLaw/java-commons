package org.the4thlaw.utils.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * General XML utilities.
 */
public final class XMLUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLUtils.class);

	private XMLUtils() {

	}

	/**
	 * Creates an {@link XMLReader} from a {@link SAXParserFactory}, taking care of XEE protection.
	 * 
	 * @return The protected {@link XMLReader}
	 * @throws ParserConfigurationException In case of SAX configuration issue (protection and SAXParser creation).
	 * @throws SAXException In case of SAX issue (protection and SAXParser/XMLReader creation).
	 */
	public static XMLReader createXmlReader() throws ParserConfigurationException, SAXException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
		spf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		spf.setNamespaceAware(true);
		SAXParser saxParser = spf.newSAXParser();
		return saxParser.getXMLReader();
	}

	/**
	 * Unconditionally close an {@link XMLStreamWriter}.
	 * <p>
	 * Equivalent to {@link XMLStreamWriter#close()}, except any exceptions will be logged and ignored. This is
	 * typically used in finally blocks.
	 * </p>
	 * <p>
	 * Similar to Apache Commons' method, but actually logs any error rather than discarding them.
	 * </p>
	 *
	 * @param closeable The object to close, may be null or already closed
	 */
	public static void closeQuietly(XMLStreamWriter closeable) {
		if (closeable == null) {
			return;
		}
		try {
			closeable.close();
		} catch (XMLStreamException e) {
			LOGGER.warn("Failed to close XMLStreamWriter", e);
		}
	}
}
