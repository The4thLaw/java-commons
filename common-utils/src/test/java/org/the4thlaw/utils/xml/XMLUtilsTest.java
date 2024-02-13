package org.the4thlaw.utils.xml;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.xml.sax.SAXException;

/**
 * Tests for {@link XMLUtils}.
 */
class XMLUtilsTest {
	/**
	 * Tests {@link XMLUtils#createXmlReader()}.
	 * 
	 * @throws ParserConfigurationException In case of test issue
	 * @throws SAXException In case of test issue
	 */
	@Test
	void createXmlReader() throws ParserConfigurationException, SAXException {
		assertThat(XMLUtils.createXmlReader()).isNotNull();
	}

    /**
	 * Tests {@link XMLUtils#closeQuietly(XMLStreamWriter)}.
	 *
	 * @throws XMLStreamException should never be thrown.
	 */
	@Test
	void testCloseQuietlyXMLStreamWriterNormal() throws XMLStreamException {
		XMLUtils.closeQuietly((XMLStreamWriter) null);
		XMLStreamWriter closeable = Mockito.mock(XMLStreamWriter.class);
		XMLUtils.closeQuietly(closeable);
		Mockito.verify(closeable, Mockito.times(1)).close();
	}

	/**
	 * Tests {@link XMLUtils#closeQuietly(XMLStreamWriter)} when the closeable throws an Exception.
	 *
	 * @throws XMLStreamException should never be thrown.
	 */
	@Test
	void testCloseQuietlyXMLStreamWriterException() throws XMLStreamException {
		XMLStreamWriter closeable = Mockito.mock(XMLStreamWriter.class);
		Mockito.doThrow(XMLStreamException.class).when(closeable).close();
		XMLUtils.closeQuietly(closeable);
		Mockito.verify(closeable, Mockito.times(1)).close();
	}
}
