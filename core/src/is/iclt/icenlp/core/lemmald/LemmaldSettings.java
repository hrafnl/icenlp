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

package is.iclt.icenlp.core.lemmald;

import java.util.Properties;

/**
 *
 * @author Anton
 */
public class LemmaldSettings {
    
    private static Properties settings;

    static {
        settings = new Properties();
        LemmaldSettings.setValue("systemOut", false );
        LemmaldSettings.setValue("compoundAnalysis", true );
        LemmaldSettings.setValue("longestMatch", true );
        LemmaldSettings.setValue("umlautSubstitution", true );
        LemmaldSettings.setValue("postFixer", true );
    }
    
    public static String getProperty( String key ){
        return settings.getProperty( key );
    }

    public static void setValue( String key, String value ){
        settings.setProperty(key, value);
    }

    public static void setValue( String key, boolean value ){
        String sValue = "off";
        if( value ){
            sValue = "on";
        }
        settings.setProperty(key, sValue);
    }
    
    public static boolean isOn( String key ){
        if( settings.containsKey(key) ){
            return settings.getProperty( key ).equals("on");
        }
        return false;
    }

}
