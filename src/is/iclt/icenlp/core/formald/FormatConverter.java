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

/**
 *
 * @author Anton Karl Ingason <anton.karl.ingason@gmail.com>
 */
public abstract class FormatConverter {

    private Format inputFormat;
    private Format outputFormat;

    public FormatConverter( Format inputFormat, Format outputFormat ){
        this.inputFormat = inputFormat;
        this.outputFormat = outputFormat;
    }

    public String convert( String data ){
        return outputFormat.encode( inputFormat.decode(data) );
    }
}
