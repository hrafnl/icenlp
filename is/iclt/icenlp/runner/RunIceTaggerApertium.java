/*
 * Copyright (C) 2009 Hrafn Loftsson
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
 * Hrafn Loftsson, School of Computer Science, Reykjavik University.
 * hrafn@ru.is
 */
package is.iclt.icenlp.runner;

import is.iclt.icenlp.core.icetagger.IceTaggerOutputApertium;

import java.io.IOException;
import java.util.Date;

/**
 * Runs IceParser producing Apertium output format.
 * @author Hrafn Loftsson
 */
public class RunIceTaggerApertium extends RunIceTagger{


    public static void main( String args[] ) throws IOException

	{
        RunIceTaggerApertium runner = new RunIceTaggerApertium();
        Date before = runner.initialize(args);

        // Instantiate an output class
        runner.iceOutput = new IceTaggerOutputApertium(runner.outputFormat, runner.separator, runner.fullOutput, runner.fullDisambiguation, runner.tagMapFile, runner.lemmatizerFile);
        // Perform the tagging
        runner.performTagging();
		
		runner.finish(before);
	}
}
