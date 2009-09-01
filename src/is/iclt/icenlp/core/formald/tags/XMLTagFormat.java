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

package is.iclt.icenlp.core.formald.tags;

import is.iclt.icenlp.core.utils.XmlOperations;

import java.io.IOException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

/**
 * An implentation of TagFormat where the tagged text is represented
 * as an XML String.
 * 
 * @see TagFormat
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public class XMLTagFormat extends TagFormat {

    private XMLTagFormat(){}
    
    @Override
    public Document decode(String data) {
        try {
            return XmlOperations.loadXMLFrom(data);
        } catch (SAXException ex) {
            System.out.println( ex.getMessage() );
            ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println( ex.getMessage() );
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String encode(Document data) {
        return XmlOperations.docToString(data);
    }
    
    public static TagFormat newInstance() {
        return new XMLTagFormat();
    }

    @Override
    public String sampleData() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+System.getProperty("line.separator")+"<taggedText><sentence><token tag=\"fp1en\" word=\"Ég\"/><token tag=\"sfg1en\" word=\"er\"/><token tag=\"lkensf\" word=\"rauður\"/><token tag=\"nken\" word=\"kaktus\"/></sentence><sentence><token tag=\"c\" word=\"og\"/><token tag=\"fp1en\" word=\"ég\"/><token tag=\"sfg1en\" word=\"ætla\"/><token tag=\"cn\" word=\"að\"/><token tag=\"sng\" word=\"spila\"/><token tag=\"faheo\" word=\"þetta\"/><token tag=\"nheo\" word=\"lag\"/><token tag=\".\" word=\".\"/></sentence></taggedText>";
    }

}
