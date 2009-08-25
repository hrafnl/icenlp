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

/**
 * An abstract class that represents a <code>Tagset</code>. A Tagset must be able
 * to provide a mapping to and from the default tagset, which in the
 * case of IceNLP is the IFD Tagset. A <code>null</code> Tagset means
 * that the default Tagset should be used.
 * <br /><br />
 * Build in Tagsets are provided as static constants.
 * A custom tagset can be handled by subclassing Tagset or by providing a
 * list of mappings to a new instance of the CustomTagset class.
 * 
 * @see CustomTagset
 * 
 * @author <a href="mailto:anton.karl.ingason@gmail.com">Anton Karl Ingason</a>
 */
public abstract class Tagset {

    public static final Tagset IFD = null;
    public static final Tagset APERTIUM = CustomTagset.newInstance( Object.class.getClass().getResourceAsStream("/dict/icetagger/otb.apertium.dict") );

    public static Tagset getDefault(){
        return IFD;
    }

    public abstract String getTag( String standardTag );
    public abstract String getStandardTag( String tag );

}
