package is.iclt.icenlp.core.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import is.iclt.icenlp.core.apertium.ApertiumEntry;
import is.iclt.icenlp.core.apertium.LtProcParser;

import org.junit.Before;
import org.junit.Test;

public class LtProcParserTests
{
	private LtProcParser ltp;
	private ArrayList<ApertiumEntry> list;
	
	@Before
	public void setUp()
	{
		String testString = "^Hann/Hann<prn><p3><m><sg><nom>/Hann<prn><p3><m><sg><acc>$ ^er/er<cnjsub>/er<rel><an><mf><sp>/vera<vbser><pri><p3><sg>/vera<vbser><pri><p1><sg>$ ^góður/góður<adj><pst><m><sg><nom><sta>$^./.<sent>$";
		
		ltp = new LtProcParser(testString);
		list = ltp.parse();
	}
	
	@Test
	public void notNull()
	{
		assertTrue("List is null.", list != null);
	}
	
	@Test
	public void correctSize()
	{
		assertTrue("List size is " + list.size() + " it should be 6.", list.size() == 6);
	}
	
	@Test
	public void correctSurfaceForms()
	{
		String errormsg = "Surface Form not correct.";
		
		assertTrue(errormsg, list.get(0).getSurfaceForm().equals("Hann"));
		assertTrue(errormsg, list.get(2).getSurfaceForm().equals("er"));
		assertTrue(errormsg, list.get(4).getSurfaceForm().equals("góður"));
		assertTrue(errormsg, list.get(5).getSurfaceForm().equals("."));
	}
	
	@Test
	public void correctLexicalUnits()
	{
		String errormsg = "Lexical unit not correct.";
		
		// Hann
		assertTrue(errormsg, list.get(0).getPossibleLexicalUnits().get(0).getLemma().equals("Hann"));
		assertTrue(errormsg, list.get(0).getPossibleLexicalUnits().get(0).getSymbols().equals("<prn><p3><m><sg><nom>"));
		
		assertTrue(errormsg, list.get(0).getPossibleLexicalUnits().get(1).getLemma().equals("Hann"));
		assertTrue(errormsg, list.get(0).getPossibleLexicalUnits().get(1).getSymbols().equals("<prn><p3><m><sg><acc>"));
		
		// er
		assertTrue(errormsg, list.get(2).getPossibleLexicalUnits().get(0).getLemma().equals("er"));
		assertTrue(errormsg, list.get(2).getPossibleLexicalUnits().get(0).getSymbols().equals("<cnjsub>"));
		
		assertTrue(errormsg, list.get(2).getPossibleLexicalUnits().get(1).getLemma().equals("er"));
		assertTrue(errormsg, list.get(2).getPossibleLexicalUnits().get(1).getSymbols().equals("<rel><an><mf><sp>"));
		
		assertTrue(errormsg, list.get(2).getPossibleLexicalUnits().get(2).getLemma().equals("vera"));
		assertTrue(errormsg, list.get(2).getPossibleLexicalUnits().get(2).getSymbols().equals("<vbser><pri><p3><sg>"));
		
		assertTrue(errormsg, list.get(2).getPossibleLexicalUnits().get(3).getLemma().equals("vera"));
		assertTrue(errormsg, list.get(2).getPossibleLexicalUnits().get(3).getSymbols().equals("<vbser><pri><p1><sg>"));
		
		// góður
		assertTrue(errormsg, list.get(4).getPossibleLexicalUnits().get(0).getLemma().equals("góður"));
		assertTrue(errormsg, list.get(4).getPossibleLexicalUnits().get(0).getSymbols().equals("<adj><pst><m><sg><nom><sta>"));
		
		// .
		assertTrue(errormsg, list.get(5).getPossibleLexicalUnits().get(0).getLemma().equals("."));
		assertTrue(errormsg, list.get(5).getPossibleLexicalUnits().get(0).getSymbols().equals("<sent>"));
	}
}
