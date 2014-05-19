package iit.cnr.iot.observer;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.ParserConfigurationException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaderJDOMFactory;
import org.jdom2.input.sax.XMLReaderXSDFactory;

public class XmlValidator {

	public enum Stato {
		Accesso, Uscita
	}
	private static String schema = "files/schema.xsd";
	private String xml;
	DocumentBuilderFactory factory;
	DocumentBuilder builder;

	public XmlValidator(String xml) {
		this.xml = xml;
	}

	public Stato parseDocument() throws JDOMException, IOException {

		File file = new File(schema);
		if(file.exists())
			System.out.println("Esiste");
		else
			System.out.println("Non esiste");
		File xsdfile = new File(schema);
		XMLReaderJDOMFactory schemafac = new XMLReaderXSDFactory(xsdfile);
		SAXBuilder builder = new SAXBuilder(schemafac);
		Document validdoc = builder.build(new StringReader(xml));

		Element rootElement = validdoc.getRootElement();;
		
		String type= rootElement.getChildText("type");
		

		if (type.equalsIgnoreCase("Accesso"))
			return Stato.Accesso;

		return Stato.Uscita;

	}
}
