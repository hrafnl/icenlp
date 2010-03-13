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

package is.iclt.icenlp.core.formald.tagsets;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * 
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public class CustomTagset extends Tagset {

    private HashMap<String, String> tagMap;
    private HashMap<String, String> reverseTagMap;

    private CustomTagset( InputStream is ) {
        try {
            ArrayList<String> lines = (ArrayList<String>) IOUtils.readLines(is);
            tagMap = new HashMap<String, String>();
            reverseTagMap = new HashMap<String, String>();
            
            for (String line : lines) {
                String[] tokens = line.split("\\s+");
                if (tokens.length == 2) {
                    tagMap.put(tokens[0], tokens[1]);
                    reverseTagMap.put( tokens[1], tokens[0] );
                }
            }
            is.close();
        } catch (IOException ex) {
            System.out.println("Could not load tagset data!");
            ex.printStackTrace();
        }
    }

    public static Tagset newInstance( InputStream is ) {
        return new CustomTagset(is);
    }

    public static Tagset newInstance( String inputFile ){
        try {
            return new CustomTagset( FileUtils.openInputStream( new File( inputFile ) ) );
        } catch ( IOException ex ){
            System.out.println( "Could not open tagset file '"+inputFile+"'!" );
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public String getTag(String standardTag) {
        return tagMap.get(standardTag);
    }

    @Override
    public String getStandardTag(String tag) {
        return reverseTagMap.get(tag);
    }

}
