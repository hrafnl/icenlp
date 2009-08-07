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
package is.iclt.icenlp.core.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.FileWriter;

/**
 * Takes care of logging output.
 * @author Hrafn Loftsson
 */
public class IceLog {
    private BufferedWriter logFile=null;    // Logfile file
    private boolean log=true;               // To log or not to log

  public IceLog(String fileName)
  throws IOException
  {
      if (!fileName.equals(""))
        logFile = new BufferedWriter(new FileWriter(fileName));
      else
          log = false;
  }

  public void log(String str)
  {
     if (log)
     try
     {
        logFile.write(str);
        logFile.newLine();
     }
     catch (IOException e) {
         System.err.println("Caught IOException: " + e.getMessage());
     }

  }

  public void close()
  {
     if (log)
     try
     {
        logFile.flush();
        logFile.close();
     }
     catch (IOException e) {
         System.err.println("Caught IOException: " + e.getMessage());
     }
  }
}
