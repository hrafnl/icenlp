/*
 * Copyright (C) 2009 Anton Karl Ingason
 *
 * This file is part of the IceNLP toolkit.
 * IceNLP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * IceNLP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with IceNLP. If not,  see <http://www.gnu.org/licenses/>.
 *
 * Contact information:
 * Anton Karl Ingason, University of Iceland.
 * anton.karl.ingason@gmail.com
 */

package is.iclt.icenlp.core.formald;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.*;

/**
 * A util class with static methods for common tasks.
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public class FormaldUtils {

	/**
	 * Writes a String to a file. Complains to system out and prints stack trace 
	 * if an IOException occurs.
	 * @param filename Name of the file to be written.
	 * @param data The String data that will be written to the file.
	 */
    public static void stringToFile( String filename, String data ){
        try {
            FileUtils.writeStringToFile(new File(filename), data);
        } catch (IOException ex) {
            System.out.println("Could not write to file '"+filename+"'!");
            ex.printStackTrace();
        }
    }

    /**
     * Reads a file to a String. Complains to system out and prints stack trace
     * if an IOException occurs.
     * @param filename Name of the file to be read.
     * @return The content of the file as a String.
     */
    public static String fileToString( String filename ){
        String contents = null;
        try {
            contents = FileUtils.readFileToString(new File(filename));
        } catch (IOException ex) {
            System.out.println("Could not read file '"+filename+"'!");
            ex.printStackTrace();
        }
        return contents;
    }

    /**
     * Returns an XML String representation of a DOM object 
     * provided as an <code>org.w3c.org.Document</code> interface.
     * @param doc The input <code>Document</code>.
     * @return An XML String representing the <code>Document</code>.
     */
    public static String docToString(Document doc) {
        //Serialize DOM
        OutputFormat format = new OutputFormat(doc);
        // as a String
        StringWriter stringOut = new StringWriter();
        XMLSerializer serial = new XMLSerializer(stringOut,
                format);
        try {
            serial.serialize(doc);
        } catch (IOException ex) {
            Logger.getLogger(FormaldUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Display the XML
        return stringOut.toString();
    }

    /**
     * Creates an instance of an empty <code>org.w3c.dom.Document</code>.
     * @return An empty <code>org.w3c.dom.Document</code>
     */
    public static org.w3c.dom.Document createDocument(){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            ex.printStackTrace();
        }
        DOMImplementation impl = builder.getDOMImplementation();

        return impl.createDocument(null,null,null);
    }

    /**
     * Loads an <code>org.w3c.dom.Document</code> from an XML String.
     * @param xml The XML to be parsed into a Document. The String must
     * be a well formed XML document.
     * @return The <code>Document</code> that results from the parse.
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public static org.w3c.dom.Document loadXMLFrom(String xml)
            throws org.xml.sax.SAXException, java.io.IOException {
        return loadXMLFrom(new java.io.ByteArrayInputStream(xml.getBytes()));
    }

    /**
     * Loads an <code>org.w3c.dom.Document</code> from an <code>InputStream</code>.
     * The InputStream must return a well formed XML Document as a String. 
     * @param is
     * @return
     * @throws org.xml.sax.SAXException
     * @throws java.io.IOException
     */
    public static org.w3c.dom.Document loadXMLFrom(java.io.InputStream is)
            throws org.xml.sax.SAXException, java.io.IOException {
        javax.xml.parsers.DocumentBuilderFactory factory =
                javax.xml.parsers.DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);        
        javax.xml.parsers.DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (javax.xml.parsers.ParserConfigurationException ex) {
        }
        org.w3c.dom.Document doc = builder.parse(is);

        is.close();
        return doc;
    }
}


