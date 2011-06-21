package is.iclt.icenlp.core.tests;


import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import is.iclt.icenlp.core.apertium.ApertiumEntry;
import is.iclt.icenlp.core.apertium.LexicalUnit;

import org.junit.Before;
import org.junit.Test;

public class ApertiumEntryTests
{
	private ApertiumEntry hann; // normal test
	private ApertiumEntry er; // verb test
	private ApertiumEntry um; // preposition test
	private ApertiumEntry mwe; // multi word expression test
	private ApertiumEntry unknown; // unknown test
	
	@Before
	public void setUp() throws Exception
	{
		// Normal test init
		ArrayList<LexicalUnit> hannLU = new ArrayList<LexicalUnit>();
		
		hannLU.add(new LexicalUnit("Hann", "<prn><p3><m><sg><nom>"));
		hannLU.add(new LexicalUnit("Hann", "<prn><p3><m><sg><acc>"));
		
		hann = new ApertiumEntry("Hann", hannLU);
		
		// Verb test init
		ArrayList<LexicalUnit> erLU = new ArrayList<LexicalUnit>();
		erLU.add(new LexicalUnit("er", "<cnjsub>"));
		erLU.add(new LexicalUnit("er", "<rel><an><mf><sp>"));
		erLU.add(new LexicalUnit("vera", "<vbser><pri><p3><sg>"));
		erLU.add(new LexicalUnit("vera", "<vbser><pri><p1><sg>"));
		
		er = new ApertiumEntry("er", erLU);
		
		// Preposition test init
		ArrayList<LexicalUnit> umLU = new ArrayList<LexicalUnit>();
		
		umLU.add(new LexicalUnit("um", "<pr>"));
		
		um = new ApertiumEntry("um", umLU);
		
		// MWE test init
		ArrayList<LexicalUnit> mweLU = new ArrayList<LexicalUnit>();
		
		mweLU.add(new LexicalUnit("eins og", "<cnjsub>"));
		
		mwe = new ApertiumEntry("eins og", mweLU);
		
		// Unknown test init
		// We use a word that is likely not to be added to any dictionary
		ArrayList<LexicalUnit> unknownLU = new ArrayList<LexicalUnit>();
		
		unknownLU.add(new LexicalUnit("Asdfjklæinn", "Asdfjklæinn", true));
		
		unknown = new ApertiumEntry("Asdfjklæinn", unknownLU);
	}
	
	@Test
	public void LexicalUnitSize()
	{
		int size = hann.getPossibleLexicalUnits().size();
		
		assertTrue("Lexical Unit size should be 2, it is "+size, size == 2);
	}
	
	@Test
	public void SurfaceForm()
	{
		String sf = hann.getSurfaceForm();
		
		assertTrue("Surface form should be 'Hann' but is '"+sf+"'", sf.equals("Hann"));
	}
	
	@Test
	public void CorrectLURemoval()
	{
		LexicalUnit remove = hann.getPossibleLexicalUnits().get(1);
		
		int sizeBefore = hann.getPossibleLexicalUnits().size();
		
		hann.removeLexicalUnit(remove);
		
		int sizeAfter = hann.getPossibleLexicalUnits().size();
		
		assertTrue("Lexical Unit removal did not remove correctly.", sizeBefore == (sizeAfter + 1));
		
		String symbols = hann.getPossibleLexicalUnits().get(0).getSymbols();
		
		assertTrue("Symbols do not match after removal.", symbols.equals("<prn><p3><m><sg><nom>"));
	}
	
	@Test
	public void VerbFinding()
	{
		assertTrue("No verbs found when they should have.", er.isAnyLuAVerb());
	}
	
	@Test
	public void PrepositionFinding()
	{
		assertTrue("No prepositions found when they should have.", um.isAnyLuPreposition());
	}
	
	@Test
	public void MultiWordExpressionFinding()
	{
		assertTrue("No multi word expressions found when they should have.", mwe.isMWE());
		assertTrue("A word that is not a multi word expression was marked as one.", !hann.isMWE());
	}
	
	@Test
	public void UnknownFinding()
	{
		assertTrue("Unknown word not marked as unknown.", unknown.isUnknown());
	}
}
