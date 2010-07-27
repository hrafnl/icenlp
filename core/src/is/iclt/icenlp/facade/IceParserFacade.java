/*
 * Copyright (C) 2009 Sverrir Sigmundarson
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
package is.iclt.icenlp.facade;

import is.iclt.icenlp.core.iceparser.*;

import java.io.*;

/**
 * Provides a simplified interface to IceParser.
 * @author Sverrir Sigmundarson
 */
public class IceParserFacade
{
	private TagEncoder tagEncdr;
    private Preprocess preprocess_scn;
    private Phrase_MWE mwe_scn;
    private Phrase_MWEP1 mwep1_scn;
    private Phrase_MWEP2 mwep2_scn;
    private Phrase_AdvP advp_scn;
    private Phrase_AP ap_scn;
    private Phrase_APs aps_scn;
    private Phrase_NP np_scn;
	private Phrase_NP2 np2_scn;
    private Phrase_VP vp_scn;
    private Case_AP cap_scn;
    private Case_NP cnp_scn;
    private Phrase_NPs nps_scn;
    private Phrase_PP pp_scn;
    private Clean1 cl1_scn;
    private Func_TIMEX f_time_scn;
    private Func_QUAL f_qual_scn;
    private Func_SUBJ f_subj_scn;
    private Func_COMP f_comp_scn;
    private Func_OBJ f_obj_scn;
    private Func_OBJ2 f_obj2_scn;
    private Func_OBJ3 f_obj3_scn;
    private Func_SUBJ2 f_subj2_scn;
    private Clean2 cl2_scn;
    private Phrase_Per_Line ppl_scn;
	private TagDecoder tagDecdr;


    public IceParserFacade()
	{
        StringReader sr = new StringReader("test");
		tagEncdr = new TagEncoder(sr);
        preprocess_scn = new Preprocess(sr);
        mwe_scn = new Phrase_MWE(sr);
        mwep1_scn = new Phrase_MWEP1(sr);
        mwep2_scn = new Phrase_MWEP2(sr);
        advp_scn = new Phrase_AdvP(sr);
        ap_scn = new Phrase_AP(sr);
        aps_scn = new Phrase_APs(sr);
        np_scn = new Phrase_NP(sr);
		np2_scn = new Phrase_NP2(sr);
        vp_scn = new Phrase_VP(sr);
        cap_scn = new Case_AP(sr);
        cnp_scn = new Case_NP(sr);
        nps_scn = new Phrase_NPs(sr);
        pp_scn = new Phrase_PP(sr);
        cl1_scn = new Clean1(sr);
        f_time_scn = new Func_TIMEX(sr);
        f_qual_scn = new Func_QUAL(sr);
        f_subj_scn = new Func_SUBJ(sr);
        f_comp_scn = new Func_COMP(sr);
        f_obj_scn = new Func_OBJ(sr);
        f_obj2_scn = new Func_OBJ2(sr);
        f_obj3_scn = new Func_OBJ3(sr);
        f_subj2_scn = new Func_SUBJ2(sr);
        cl2_scn = new Clean2(sr);
        ppl_scn = new Phrase_Per_Line(sr);
		tagDecdr = new TagDecoder(sr);
    }

	private void print( String text )
	{
		//System.out.println( text );
	}

	public String parse( String text ) throws IOException
	{
		return parse( text, false, false );
	}

	public String parse( String text, boolean include_func ) throws IOException
	{
		return parse( text, include_func, false );
	}
	public String parse( String text, boolean include_func, boolean one_phrase_per_line ) throws IOException
	{
		return parse( text, include_func, one_phrase_per_line, false );
	}
	public String parse( String text, boolean include_func, boolean one_phrase_per_line , boolean agreement) throws IOException
	{
		return parse( text, include_func, one_phrase_per_line, agreement, false);
	}
	public String parse( String text, boolean include_func, boolean one_phrase_per_line , boolean agreement, boolean markGrammarError) throws IOException
	{
		
		// --------------------------------
        //print( "tagEncdr" );
		StringReader sr = new StringReader( text );
		StringWriter sw = new StringWriter( );
		
		tagEncdr.yyclose();
		tagEncdr.yyreset(sr);
		tagEncdr.parse(sw);		

		// --------------------------------
        //print( "preprocess" );
        sr = new StringReader( sw.toString() );
        sw = new StringWriter( );


        preprocess_scn.yyclose();
        preprocess_scn.yyreset(sr);
        preprocess_scn.parse(sw);

		// --------------------------------
		//print( "Phrase_MWE" );

        sr = new StringReader( sw.toString() );
        sw = new StringWriter( );

        mwe_scn.yyclose();
        mwe_scn.yyreset(sr);
        mwe_scn.parse(sw);

        // --------------------------------
		//print( "Phrase_MWEP1" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        mwep1_scn.yyclose();
        mwep1_scn.yyreset(sr);
        mwep1_scn.parse(sw);

        // --------------------------------
		//print( "Phrase_MWEP2" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        mwep2_scn.yyclose();
        mwep2_scn.yyreset(sr);
        mwep2_scn.parse(sw);

        // --------------------------------
		//print( "Phrase_AdvP" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        advp_scn.yyclose();
        advp_scn.yyreset(sr);
        advp_scn.parse(sw);

        // --------------------------------
		//print( "Phrase_AP" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        ap_scn.yyclose();
        ap_scn.yyreset(sr);
        ap_scn.parse(sw);

        // --------------------------------
		//print( "Case_AP" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        cap_scn.yyclose();
        cap_scn.yyreset(sr);
        cap_scn.parse(sw);

        // --------------------------------
		//print( "Phrase_APs" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        aps_scn.yyclose();
        aps_scn.yyreset(sr);
        aps_scn.parse(sw);

        // --------------------------------
		//print( "Phrase_NP" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );
		if(agreement)
		{
			np_scn.set_doAgreementCheck(true);
		}
		if(markGrammarError)
		{
			np_scn.set_markGrammarError(true);
		}
        np_scn.yyclose();
        np_scn.yyreset(sr);
        np_scn.parse(sw);
		
		if(agreement && !markGrammarError)
		{
			// --------------------------------
        	//print( "phrase_NP2" );
			sr = new StringReader( sw.toString() );
			sw = new StringWriter();
			
			np2_scn.yyclose();
			np2_scn.yyreset(sr);
			np2_scn.parse(sw);
		}

        // --------------------------------
		//print( "Phrase_VP" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        vp_scn.yyclose();
        vp_scn.yyreset(sr);
        vp_scn.parse(sw);

        // --------------------------------
		//print( "Case_NP" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        cnp_scn.yyclose();
        cnp_scn.yyreset(sr);
        cnp_scn.parse(sw);

        // --------------------------------
		//print( "Phrase_NPs" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        nps_scn.yyclose();
        nps_scn.yyreset(sr);
        nps_scn.parse(sw);

        // --------------------------------
		//print( "Phrase_PP" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        pp_scn.yyclose();
        pp_scn.yyreset(sr);
        pp_scn.parse(sw);

        // --------------------------------
		//print( "Clean1" );
		sr = new StringReader( sw.toString() );
		sw = new StringWriter( );

        cl1_scn.yyclose();
        cl1_scn.yyreset(sr);
        cl1_scn.parse(sw);

        //print( "1:"+sw.toString() );

		if( include_func )
		{
            //print( "Func_TIMEX" );
            sr = new StringReader( sw.toString() );
            sw = new StringWriter( );

            f_time_scn.yyclose();
            f_time_scn.yyreset(sr);
            f_time_scn.parse(sw);
			            
            // --------------------------------
			//print( "Func_QUAL" );
			sr = new StringReader( sw.toString() );
			sw = new StringWriter( );

            f_qual_scn.yyclose();
            f_qual_scn.yyreset(sr);
            f_qual_scn.parse(sw);

            // --------------------------------
			//print( "Func_SUBJ" );
			sr = new StringReader( sw.toString() );
			sw = new StringWriter( );
			if(agreement)
			{
				f_subj_scn.set_doAgreementCheck(true);
			}
			if(markGrammarError)
			{
				f_subj_scn.set_markGrammarError(true);
			}
            f_subj_scn.yyclose();
            f_subj_scn.yyreset(sr);
            f_subj_scn.parse(sw);

            // --------------------------------
			//print( "Func_COMP" );
			sr = new StringReader( sw.toString() );
			sw = new StringWriter( );

            f_comp_scn.yyclose();
            f_comp_scn.yyreset(sr);
            f_comp_scn.parse(sw);

            // --------------------------------
			//print( "Func_OBJ" );
			sr = new StringReader( sw.toString() );
			sw = new StringWriter( );

            f_obj_scn.yyclose();
            f_obj_scn.yyreset(sr);
            f_obj_scn.parse(sw);

            // --------------------------------
			//print( "Func_OBJ2" );
			sr = new StringReader( sw.toString() );
			sw = new StringWriter( );

            f_obj2_scn.yyclose();
            f_obj2_scn.yyreset(sr);
            f_obj2_scn.parse(sw);

            // --------------------------------
			//print( "Func_OBJ3" );
			sr = new StringReader( sw.toString() );
			sw = new StringWriter( );

            f_obj3_scn.yyclose();
            f_obj3_scn.yyreset(sr);
            f_obj3_scn.parse(sw);

            // --------------------------------
			//print( "Func_SUBJ2" );
			sr = new StringReader( sw.toString() );
			sw = new StringWriter( );

            f_subj2_scn.yyclose();
            f_subj2_scn.yyreset(sr);
            f_subj2_scn.parse(sw);
		}

		// --------------------------------
		//print( "Clean3" );
        sr = new StringReader( sw.toString() );
        sw = new StringWriter( );

        cl2_scn.yyclose();
        cl2_scn.yyreset(sr);
        cl2_scn.parse(sw);

		if( one_phrase_per_line )
		{
            sr = new StringReader( sw.toString() );
            sw = new StringWriter( );
			// --------------------------------
			//print( "Phrase_Per_Line" );

            ppl_scn.yyclose();
            ppl_scn.yyreset(sr);
            ppl_scn.parse(sw);
		}

		// --------------------------------
		//print( "tagDecdr" );
        sr = new StringReader( sw.toString() );
        sw = new StringWriter( );		

		tagDecdr.yyclose();
		tagDecdr.yyreset(sr);
		tagDecdr.parse(sw);


		return sw.toString();
	}

	public static void main( String[] args )
	{
		System.out.println( "Testing parser" );
		long start = System.currentTimeMillis();

		IceParserFacade pfac = new IceParserFacade();
		try
		{
			String parsed = pfac.parse( "Stóru lkfnvf strákarnir nkfng leika sfg3fn sér fpkfþ við aþ sætu lheþsf stúlkurnar nvfng í aþ rauðu lhfþvf pilsunum nhfþg . .", true, true );

			System.out.println( parsed );
		}
		catch( IOException e )
		{
			e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
		}
		long end = System.currentTimeMillis();

		long duration = (end-start);
		System.out.println( "Time (msec):"+duration );


	}
}
