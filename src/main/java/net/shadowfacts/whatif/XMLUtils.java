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
public class XMLUtils {

	public static Document parseDOM(File f) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(f);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (ParserConfigurationException | SAXException | IOException e) {
			System.err.println("Problem parsing DOM from file at " + f.getPath());
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

}
