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

import java.util.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A class for storing and accessing a lexicon.
 */
public class Lexicon {
	/**
	 * This class is based on the Lexicon class by Oliver Mason from the book
	 * Programming for Corpus Linguistics: How to Do Text Analysis with Java
	 */
	protected PropertiesEncoding storage = null;

	/**
	 * Constructor *
	 */
	public Lexicon() {
		storage = new PropertiesEncoding();
	}

	/**
	 * Load a lexicon file. The lexicon is initialised from a file. The file
	 * should contain one line per entry, with the data separated from the entry
	 * with an equal-sign (the standard Java property format).
	 */

	public Lexicon(InputStream in) throws IOException, NullPointerException {
		if (in == null)
			throw new NullPointerException(
					"InputStream was not initialized correctly (null)");

		storage = new PropertiesEncoding();
		load(in);
	}

	public Lexicon(String filename) throws IOException {
		storage = new PropertiesEncoding();
		load(filename);
	}

	public PropertiesEncoding getProperties() {
		return storage;
	}

	public int getSize() {
		return storage.size();
	}

	public boolean containsKey(String key) {
		return storage.containsKey(key);
	}

	public void put(String key, String value) {
		storage.put(key, value);
	}

	public Set getEntrySet() {
		return storage.entrySet();
	}

	public void load(String filename) throws IOException {
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				filename));
		storage.load(in); // Important to call the overridden method in
							// PropertiesEncoding
		in.close();
	}

	public void load(InputStream in) throws IOException {
		storage.load(in); // Important to call the overridden method in
							// PropertiesEncoding
		in.close();
	}

	public Enumeration keys() {
		return storage.keys();
	}

	/**
	 * Look up a word in the lexicon. If the given word form is contained in the
	 * lexicon, the associated entry is returned as a String, otherwise null.
	 */
	public String lookup(String word, boolean ignoreCase)
	{
		String lookupWord;

		if(ignoreCase)
		{
			lookupWord = word.toLowerCase();
		}
		else
		{
			lookupWord = word;
		}

		return storage.getProperty(lookupWord, null);
	}

} // end of class is.iclt.icenlp.core.utils.Lexicon