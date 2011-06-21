package is.iclt.icenlp.core.tests;

import static org.junit.Assert.assertTrue;
import is.iclt.icenlp.core.apertium.LexicalUnit;

import org.junit.Before;
import org.junit.Test;

public class LexicalUnitTests
{
	private LexicalUnit preposition;
	private LexicalUnit verb;
	private LexicalUnit det;
	private LexicalUnit unknown;
	private LexicalUnit space;
	
	@Before
	public void setUp() throws Exception
	{
		preposition = new LexicalUnit("um", "<pr>");
		verb = new LexicalUnit("er", "<vbser><pri><p3><sg>");
		det = new LexicalUnit("hinn", "<det><def><m><sg><nom>");
		unknown = new LexicalUnit("Asdfjklæinn", "Asdfjklæinn", true);
		space = new LexicalUnit(" ", " ", true, true);
	}
	
	@Test
	public void PrepositionTest()
	{
		assertTrue("Lexical Unit not a preposition when it should be.", preposition.isPreposition());
		assertTrue("Lexical Unit is a verb when it should not be.", !preposition.isVerb());
		assertTrue("Lexical Unit is a determinant when it should not be.", !preposition.isDet());
	}
	
	@Test
	public void VerbTest()
	{
		assertTrue("Lexical Unit not a verb when it should be.", verb.isVerb());
		assertTrue("Lexical Unit is a determinant when it should not be.", !verb.isDet());
		assertTrue("Lexical Unit is a preposition when it should not be.", !verb.isPreposition());
	}
	
	@Test
	public void DetTest()
	{
		assertTrue("Lexical Unit not a determinant when it should be.", det.isDet());
		assertTrue("Lexical Unit is a verb when it should not be.", !det.isVerb());
		assertTrue("Lexical Unit is a preposition when it should not be.", !det.isPreposition());
	}
	
	@Test
	public void UnknownTest()
	{
		assertTrue("Lexical Unit not unknown word when it should be.", unknown.isUnknown());
		assertTrue("Lexical Unit is a verb when it should not be.", !unknown.isVerb());
		assertTrue("Lexical Unit is a determinant when it should not be.", !unknown.isDet());
		assertTrue("Lexical Unit is a preposition when it should not be.", !unknown.isPreposition());
	}
	
	@Test
	public void SpaceTest()
	{
		assertTrue("Lexical Unit not a space word when it should be.", space.isSpace());
		assertTrue("Lexical Unit is a verb when it should not be.", !space.isVerb());
		assertTrue("Lexical Unit is a determinant when it should not be.", !space.isDet());
		assertTrue("Lexical Unit is a preposition when it should not be.", !space.isPreposition());
	}
}
