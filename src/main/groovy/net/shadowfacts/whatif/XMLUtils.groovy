package net.shadowfacts.whatif;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * @author shadowfacts
 */
class XMLUtils {

	static Document parseDOM(File f) {
		def factory = DocumentBuilderFactory.newInstance();
		def builder = factory.newDocumentBuilder();
		def doc = builder.parse(f)
		doc.getDocumentElement().normalize()
		return doc
	}

}
