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
package is.iclt.icenlp.core.icemorphy;

import is.iclt.icenlp.core.utils.IceTag;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: hrafn
 * Date: 21.11.2009
 * Time: 14:32:50
 * To change this template use File | Settings | File Templates.
 */
public class MorphoRules {
    public ArrayList<MorphoRuleNounAdjective> listNounArticle;
    public ArrayList<MorphoRuleNounAdjective> listNounAdjectiveSingular;
    public ArrayList<MorphoRuleNounAdjective> listNounAdjectivePlural;
    public ArrayList<MorphoRuleVerbFinite> listVerbFinite;
    public ArrayList<MorphoRuleVerb> listVerb;
    public ArrayList<MorphoRuleVerbPastParticiple> listVerbPastParticiple;

    public MorphoRules() {
         createNounArticleRules();
         createNounAdjectiveSingularRules();
         createNounAdjectivePluralRules();
         createVerbFiniteRules();
         createVerbRules();
         createVerbPastParticipleRules();
    }

    public void createNounArticleRules() {
            
            listNounArticle = new ArrayList<MorphoRuleNounAdjective>();
            // hest-urinn
            listNounArticle.add(new MorphoRuleNounAdjective("urinn", 5, IceMorphy.MorphoClass.NounMasculine1, true, false, false, false, IceTag.cMasculine, IceTag.cSingular));
            // gítar-inn
            listNounArticle.add(new MorphoRuleNounAdjective("arinn", 3, IceMorphy.MorphoClass.NounMasculine8,  true, true, false, false, IceTag.cMasculine, IceTag.cSingular));
            // kennar-inn
            listNounArticle.add(new MorphoRuleNounAdjective("arinn", 3, IceMorphy.MorphoClass.NounMasculine2,  true, false, false, false, IceTag.cMasculine, IceTag.cSingular));

            // hest-arnir
            listNounArticle.add(new MorphoRuleNounAdjective("arnir", 5, IceMorphy.MorphoClass.NounMasculine1,  true, false, false, false, IceTag.cMasculine, IceTag.cPlural ));
            // gest-irnir
            listNounArticle.add(new MorphoRuleNounAdjective("irnir", 5, IceMorphy.MorphoClass.NounMasculine1,  true, false, false, false, IceTag.cMasculine, IceTag.cPlural ));

            // hest-inn
            listNounArticle.add(new MorphoRuleNounAdjective("inn", 3, IceMorphy.MorphoClass.NounMasculine1,  false, true, false, false, IceTag.cMasculine, IceTag.cSingular ));
			// skól-inn
            listNounArticle.add(new MorphoRuleNounAdjective("inn", 3, IceMorphy.MorphoClass.NounMasculine2,  true, false, false, false, IceTag.cMasculine, IceTag.cSingular ));
            // skól-ann
            listNounArticle.add(new MorphoRuleNounAdjective("ann", 3 , IceMorphy.MorphoClass.NounMasculine2,  false, true, false, false, IceTag.cMasculine, IceTag.cSingular ));

            // bekk-junum
            listNounArticle.add(new MorphoRuleNounAdjective("junum", 5, IceMorphy.MorphoClass.NounMasculine9,  false, false, true, false, IceTag.cMasculine, IceTag.cPlural ));
            // dekk-junum
            listNounArticle.add(new MorphoRuleNounAdjective("junum", 5, IceMorphy.MorphoClass.NounFeminine1,  false, false, true, false, IceTag.cFeminine, IceTag.cPlural));
			// hest-unum
            listNounArticle.add(new MorphoRuleNounAdjective("unum", 4, IceMorphy.MorphoClass.NounMasculine1,  false, false, true, false, IceTag.cMasculine, IceTag.cPlural ));
            // von-unum
            listNounArticle.add(new MorphoRuleNounAdjective("unum", 4, IceMorphy.MorphoClass.NounFeminine2,  false, false, true, false, IceTag.cFeminine, IceTag.cPlural));
            // svín-unum
            listNounArticle.add(new MorphoRuleNounAdjective("unum", 4, IceMorphy.MorphoClass.NounNeuter1,  false, false, true, false, IceTag.cNeuter, IceTag.cPlural));
            // nýr-unum
            listNounArticle.add(new MorphoRuleNounAdjective("unum", 4, IceMorphy.MorphoClass.NounNeuter4,  false, false, true, false, IceTag.cNeuter, IceTag.cPlural));

            // hest-inum
            listNounArticle.add(new MorphoRuleNounAdjective("inum", 4, IceMorphy.MorphoClass.NounMasculine1,  false, false, true, false, IceTag.cMasculine, IceTag.cSingular));
            // skól-anum
            listNounArticle.add(new MorphoRuleNounAdjective("anum", 4, IceMorphy.MorphoClass.NounMasculine2,  false, false, true, false, IceTag.cMasculine, IceTag.cSingular));
            // bíl-num, handlegg-num
            listNounArticle.add(new MorphoRuleNounAdjective("num", 3, IceMorphy.MorphoClass.NounMasculine1,  false, false, true, false, IceTag.cMasculine, IceTag.cSingular));

            // hest-sins
            listNounArticle.add(new MorphoRuleNounAdjective("sins", 4, IceMorphy.MorphoClass.NounMasculine1,  false, false, false, true, IceTag.cMasculine, IceTag.cSingular));
            // svín-sins
            listNounArticle.add(new MorphoRuleNounAdjective("sins", 4, IceMorphy.MorphoClass.NounNeuter1,  false, false, false, true, IceTag.cNeuter, IceTag.cSingular));
            // gerpi-sins
            listNounArticle.add(new MorphoRuleNounAdjective("sins", 5, IceMorphy.MorphoClass.NounNeuter2,  false, false, false, true, IceTag.cNeuter, IceTag.cSingular));

            // skól-ans
            listNounArticle.add(new MorphoRuleNounAdjective("ans", 3, IceMorphy.MorphoClass.NounMasculine2,  false, false, false, true, IceTag.cMasculine, IceTag.cSingular));

            // svip-una
            listNounArticle.add(new MorphoRuleNounAdjective("una", 3, IceMorphy.MorphoClass.NounFeminine1,  false, true, false, false, IceTag.cFeminine, IceTag.cSingular));

		    // von-ina
            listNounArticle.add(new MorphoRuleNounAdjective("ina", 3, IceMorphy.MorphoClass.NounFeminine2,  false, true, false, false, IceTag.cFeminine, IceTag.cSingular));
            // kæti-na
            listNounArticle.add(new MorphoRuleNounAdjective("ina", 2, IceMorphy.MorphoClass.NounFeminine4,  false, true, false, false, IceTag.cFeminine, IceTag.cSingular ));
			// gest-ina
            listNounArticle.add(new MorphoRuleNounAdjective("ina", 3, IceMorphy.MorphoClass.NounMasculine1,  false, true, false, false, IceTag.cMasculine, IceTag.cPlural ));

            // súp-nanna
            listNounArticle.add(new MorphoRuleNounAdjective("nanna", 5, IceMorphy.MorphoClass.NounFeminine1,  false, false, false, true, IceTag.cFeminine, IceTag.cPlural ));

            // hest-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, IceMorphy.MorphoClass.NounMasculine1,  false, false, false, true, IceTag.cMasculine, IceTag.cPlural ));
            // súp-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, IceMorphy.MorphoClass.NounFeminine1,  false, false, false, true, IceTag.cFeminine, IceTag.cPlural ));
            // svín-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, IceMorphy.MorphoClass.NounNeuter1,  false, false, false, true, IceTag.cNeuter, IceTag.cPlural ));
            // skól-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, IceMorphy.MorphoClass.NounMasculine2,  false, false, false, true, IceTag.cMasculine, IceTag.cPlural ));
            // von-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, IceMorphy.MorphoClass.NounFeminine2,  false, false, false, true, IceTag.cFeminine, IceTag.cPlural ));
            // efn-anna
            listNounArticle.add(new MorphoRuleNounAdjective("anna", 4, IceMorphy.MorphoClass.NounNeuter2,  false, false, false, true, IceTag.cNeuter, IceTag.cPlural ));

            // von-arinnar
            listNounArticle.add(new MorphoRuleNounAdjective("innar", 7, IceMorphy.MorphoClass.NounFeminine2,  false, false, false, true, IceTag.cFeminine, IceTag.cSingular ));
            // fræði-nnar
            listNounArticle.add(new MorphoRuleNounAdjective("innar", 4, IceMorphy.MorphoClass.NounFeminine4,  false, false, false, true, IceTag.cFeminine, IceTag.cSingular ));

            // súp-unnar
            listNounArticle.add(new MorphoRuleNounAdjective("unnar", 5, IceMorphy.MorphoClass.NounFeminine1,  false, false, false, true, IceTag.cFeminine, IceTag.cSingular ));

            // súp-urnar
            listNounArticle.add(new MorphoRuleNounAdjective("urnar", 5, IceMorphy.MorphoClass.NounFeminine1,  true, true, false, false, IceTag.cFeminine, IceTag.cPlural ));
            // von-irnar
            listNounArticle.add(new MorphoRuleNounAdjective("irnar", 5, IceMorphy.MorphoClass.NounFeminine2,  true, true, false, false, IceTag.cFeminine, IceTag.cPlural ));
            listNounArticle.add(new MorphoRuleNounAdjective("arnar", 5, IceMorphy.MorphoClass.NounFeminine2,  true, true, false, false, IceTag.cFeminine, IceTag.cPlural ));

            // súp-unni
            listNounArticle.add(new MorphoRuleNounAdjective("unni", 4, IceMorphy.MorphoClass.NounFeminine1,  false, false, true, false, IceTag.cFeminine, IceTag.cSingular ));
            // von-inni
            listNounArticle.add(new MorphoRuleNounAdjective("inni", 4, IceMorphy.MorphoClass.NounFeminine2,  false, false, true, false, IceTag.cFeminine, IceTag.cSingular ));
            // kæti-nni
            listNounArticle.add(new MorphoRuleNounAdjective("inni", 3, IceMorphy.MorphoClass.NounFeminine4,  false, false, true, false, IceTag.cFeminine, IceTag.cSingular ));

            // hest-ana
            listNounArticle.add(new MorphoRuleNounAdjective("ana", 3, IceMorphy.MorphoClass.NounMasculine1,  false, true, false, false, IceTag.cMasculine, IceTag.cPlural ));

            // svín-inu
            listNounArticle.add(new MorphoRuleNounAdjective("inu", 3, IceMorphy.MorphoClass.NounNeuter1,  false, false, true, false, IceTag.cNeuter, IceTag.cSingular ));
            // vesk-inu
            listNounArticle.add(new MorphoRuleNounAdjective("inu", 3, IceMorphy.MorphoClass.NounNeuter2,  false, false, true, false, IceTag.cNeuter, IceTag.cSingular ));

            // von-in
            listNounArticle.add(new MorphoRuleNounAdjective("in", 2, IceMorphy.MorphoClass.NounFeminine2,  true, false, false, false, IceTag.cFeminine, IceTag.cSingular ));
            // meining-in
            listNounArticle.add(new MorphoRuleNounAdjective("in", 2, IceMorphy.MorphoClass.NounFeminine3,  true, false, false, false, IceTag.cFeminine, IceTag.cSingular ));
            // svín-in
            listNounArticle.add(new MorphoRuleNounAdjective("in", 2, IceMorphy.MorphoClass.NounNeuter1,  true, true, false, false, IceTag.cNeuter, IceTag.cPlural ));
            // vesk-in
            listNounArticle.add(new MorphoRuleNounAdjective("in", 2, IceMorphy.MorphoClass.NounNeuter2,  true, true, false, false, IceTag.cNeuter, IceTag.cPlural ));
            // kæti-n
            listNounArticle.add(new MorphoRuleNounAdjective("in", 1, IceMorphy.MorphoClass.NounFeminine4,  true, false, false, false, IceTag.cFeminine, IceTag.cSingular ));

            // súp-an
            listNounArticle.add(new MorphoRuleNounAdjective("an", 2, IceMorphy.MorphoClass.NounFeminine1,  true, false, false, false, IceTag.cFeminine, IceTag.cSingular ));

            // svín-ið
            listNounArticle.add(new MorphoRuleNounAdjective("ið", 2, IceMorphy.MorphoClass.NounNeuter1,  true, true, false, false, IceTag.cNeuter, IceTag.cSingular ));
            // vesk-ið
            listNounArticle.add(new MorphoRuleNounAdjective("ið", 2, IceMorphy.MorphoClass.NounNeuter2,  true, true, false, false, IceTag.cNeuter, IceTag.cSingular ));
    }
    
    public void createNounAdjectiveSingularRules() {
            
            listNounAdjectiveSingular = new ArrayList<MorphoRuleNounAdjective>();
            // krist-inn
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("inn", 3, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cMasculine, IceTag.cSingular,  IceTag.cStrong ));
            // krist-in
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("in", 2, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));
            // krist-ins
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ins", 3, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // krist-ið
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ið", 2, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));
            // frið, smið,
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ið", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hlið
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ið", 0, IceMorphy.MorphoClass.NounNeuter1,  IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // krist-na
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("na", 2, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));
            // krist-inni
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("inni", 4, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));
            // krist-innar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("innar", 5, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // krist-num
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("num", 3, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // krist-nu
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("nu", 2, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));
            // krist-nu weak
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("nu", 2, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak ));

            // hor-aðan
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aðan", 4, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // þreytt-an
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("an", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // heil-an
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("an", 2, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // hor-aður
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aður", 4, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // iðnað-ur
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aður", 2, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // hor-uðum
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("uðum", 4, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // hor-aðrar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aðrar", 5, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // hor-aðri
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aðri", 4, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // hor-aðs
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aðs", 3, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // hor-aða weak and strong
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aða", 3, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aða", 3, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aða", 3, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, true, true, true, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aða", 3, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong, false ));

            // hor-uðu weak feminine and strong neuter
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("uðu", 3, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("uðu", 3, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong, false ));


            // hor-að
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("að", 2, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));


            // hreyfingarlaus-t
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aust", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));

            // hreyfingarlaus
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("aus", 0, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // atvinnurekand-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("andi", 1, IceMorphy.MorphoClass.NounMasculine10, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // atvinnurekand-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("anda", 1, IceMorphy.MorphoClass.NounMasculine10, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // hest-ur
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ur", 2, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gald-ur
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ur", 2, IceMorphy.MorphoClass.NounMasculine3, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hreið-ur
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ur", 2, IceMorphy.MorphoClass.NounNeuter3, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // falleg-ur
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ur", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // falleg-um
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // heil-um
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));

            // falleg-a weak and strong
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ega", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ega", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ega", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, true, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cWeak, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ega", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong, false ));

            // falleg-t
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("egt", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));
            // falleg-u
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("egu", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong, true ));
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("egu", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak, false ));

            // falleg
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("eg", 0, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // þreytt-rar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("rar", 3, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));
            // lif-rar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("rar", 3, IceMorphy.MorphoClass.NounFeminine6, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // meining-ar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("gar", 2, IceMorphy.MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // meining-u
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ingu", 1, IceMorphy.MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, false, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // meining
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ing", 0, IceMorphy.MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // hernað-ar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ðar", 2, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hlið-ar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ðar", 2, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // vitj-unar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("unar", 4, IceMorphy.MorphoClass.NounFeminine5, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // kennar-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ari", 1, IceMorphy.MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // kennar-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ara", 1, IceMorphy.MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // bekk-jar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("kkjar", 3, IceMorphy.MorphoClass.NounMasculine9, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // fund-ar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ndar", 4, IceMorphy.MorphoClass.NounMasculine5, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // von-ar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ar", 2, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gítar
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ar", 0, IceMorphy.MorphoClass.NounMasculine8, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // vitj-un
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("un", 2, IceMorphy.MorphoClass.NounFeminine5, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // hreið-urs
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("urs", 3, IceMorphy.MorphoClass.NounNeuter3, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // gald-urs
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("urs", 3, IceMorphy.MorphoClass.NounMasculine3, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // jöfn-uð
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("uð", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hor-uð
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("uð", 2, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));


            // merk-is
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("is", 2, IceMorphy.MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // lífeyr-is
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("is", 2, IceMorphy.MorphoClass.NounMasculine7, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // hest-s
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("s", 1, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gítar-s
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("s", 1, IceMorphy.MorphoClass.NounMasculine8, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // svín-s
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("s", 1, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // sérstak-s
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("s", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));


            // skól-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // kon-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // þreytt-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong));
            //listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cWeak, true ));
            //listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cWeak, false ));

            // heil-a
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong));
            //listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cMasculine, IceTag.cSingular, IceTag.cWeak, true ));
            //listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cNeuter, IceTag.cSingular, IceTag.cWeak, false ));

            // súp-u
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("u", 1, IceMorphy.MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // góð-u weak and strong
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("u", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cWeak));
            //listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("u", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong, false ));
      
            // hreið-ri
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ri", 2, IceMorphy.MorphoClass.NounNeuter3, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // gald-ri
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ri", 2, IceMorphy.MorphoClass.NounMasculine3, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // þreytt-ri
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ri", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // hest-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // skól-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, IceMorphy.MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // svín-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // vesk-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, IceMorphy.MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // hell-i
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, IceMorphy.MorphoClass.NounMasculine7, IceTag.WordClass.wcNoun, false, true, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // feiti
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 0, IceMorphy.MorphoClass.NounFeminine4, IceTag.WordClass.wcNoun, true, true, true, true, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // þreytt-i weak
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("i", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cWeak ));

            // heil-l
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ll", 1, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cStrong ));
            // stól-l
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ll", 1, IceMorphy.MorphoClass.NounMasculine6, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // fall
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ll", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

            // fíkn,
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("kn", 0, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // tákn
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("kn", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

            // gang
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ng", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // lyng
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ng", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun,  true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

            // gólf, hólf, golf
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("lf", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

            // stól, kjól
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("l", 0, IceMorphy.MorphoClass.NounMasculine6, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // fól, gól
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("l", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // heil
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("l", 0, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // pott
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("tt", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gátt
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("tt", 0, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gott
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("tt", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // þreytt
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("tt", 0, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));

            // slakt, frekt,
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("kt", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));
            // svart, bert,
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("rt", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cStrong ));

            // hest
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("t", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // kúnst
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("t", 0, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));

            // stubb
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("bb", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // gubb
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("bb", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

            // kopp
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("pp", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // grikk
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ikk", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hrekk
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ekk", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // flokk
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("kk", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // keim, Ásgrím
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("m", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension));

            // tap
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("p", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // skáp
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("p", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // odd
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("d", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));

            // mynd
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("d", 0, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // umdeild
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("d", 0, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // bak, lak
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("k", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // bók, blók
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("k", 0, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, true, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hrauk
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("k", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));


            // barð
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ð", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));
            // arð
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ð", 0, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hliðstæð
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("ð", 0, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cFeminine, IceTag.cSingular, IceTag.cStrong ));

            // bar, skúr, skór
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("r", 0, IceMorphy.MorphoClass.NounMasculine4, IceTag.WordClass.wcAdj, true, true, true, false, IceTag.cMasculine, IceTag.cSingular, IceTag.cNoDeclension ));
            // hár, tár
            listNounAdjectiveSingular.add(new MorphoRuleNounAdjective("r", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cSingular, IceTag.cNoDeclension ));

    }

    public void createNounAdjectivePluralRules() {

            listNounAdjectivePlural = new ArrayList<MorphoRuleNounAdjective>();
            // krist-inna
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("inna", 4, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("inna", 4, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("inna", 4, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // krist-nar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("nar", 3, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong ));
            // krist-in
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("in", 2, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong ));

            // hor-uðum
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("uðum", 4, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("uðum", 4, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("uðum", 4, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // hor-aða
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("aða", 3, IceMorphy.MorphoClass.Adj4, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // eng-jum, heng-jum
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("gjum", 3, IceMorphy.MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // bekk-jum
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("kkjum", 3, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // dekk-jum
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("kkjum", 3, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));

            // heil-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("lum", 3, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("lum", 3, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("lum", 3, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // vitj-unum
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("unum", 4, IceMorphy.MorphoClass.NounFeminine5, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
        
            // kristn-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("num", 3, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("num", 3, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("num", 3, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // falleg-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // súp-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // von-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // meining-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // hest-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // skól-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // lán-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // gerp-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // staur-um
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("um", 2, IceMorphy.MorphoClass.NounMasculine4, IceTag.WordClass.wcNoun, false, false, true, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // há-rra
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("rra", 3, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("rra", 3, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("rra", 3, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // þreytt-ra
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ra", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong, true));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ra", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong, true ));
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ra", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cStrong, false ));

            // gald-ra
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ra", 2, IceMorphy.MorphoClass.NounMasculine3, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // gald-rar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("rar", 3, IceMorphy.MorphoClass.NounMasculine3, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // þreytt-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong ));
            // heil-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cStrong ));
            // meining-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, IceMorphy.MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            //  hest-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // skól-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, IceMorphy.MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // maur-ar
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ar", 2, IceMorphy.MorphoClass.NounMasculine4, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // hug-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ga", 1, IceMorphy.MorphoClass.NounMasculine5, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // meining-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ga", 1, IceMorphy.MorphoClass.NounFeminine3, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // falleg-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ga", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // verk-ja
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ja", 2, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // merk-ja
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ja", 2, IceMorphy.MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));

            // búð-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ða", 1, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // minnismið-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ða", 1, IceMorphy.MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // markað-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ða", 1, IceMorphy.MorphoClass.NounMasculine5, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // súp-na
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("[^a]na", 2, IceMorphy.MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // krist-na
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("na", 2, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // hest-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // skól-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, false, true, false, true, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // svín-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // kvikind-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.NounNeuter2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));
            // súp-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // von-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, false, false, false, true, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // þreytt-a
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("a", 1, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, false, true, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // áheyrend-ur
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ndur", 2, IceMorphy.MorphoClass.NounMasculine2, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));

            // súp-ur
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ur", 2, IceMorphy.MorphoClass.NounFeminine1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));

            // vitj-anir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("anir", 4, IceMorphy.MorphoClass.NounFeminine5, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));

            // krist-nir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("nir", 3, IceMorphy.MorphoClass.Adj2, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // von-ir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ir", 2, IceMorphy.MorphoClass.NounFeminine2, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cFeminine, IceTag.cPlural, IceTag.cNoDeclension ));
            // tug-ir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ir", 2, IceMorphy.MorphoClass.NounMasculine1, IceTag.WordClass.wcNoun, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cNoDeclension ));
            // þreytt-ir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ir", 2, IceMorphy.MorphoClass.Adj1, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));
            // heil-ir
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("ir", 2, IceMorphy.MorphoClass.Adj5, IceTag.WordClass.wcAdj, true, false, false, false, IceTag.cMasculine, IceTag.cPlural, IceTag.cStrong ));

            // gólf
            listNounAdjectivePlural.add(new MorphoRuleNounAdjective("lf", 0, IceMorphy.MorphoClass.NounNeuter1, IceTag.WordClass.wcNoun, true, true, false, false, IceTag.cNeuter, IceTag.cPlural, IceTag.cNoDeclension ));

    }

    public void createVerbFiniteRules() {

            listVerbFinite = new ArrayList<MorphoRuleVerbFinite>();

            // þeir/ég borða
            listVerbFinite.add(new MorphoRuleVerbFinite("a", 1, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cFirstPerson, IceTag.cSingular ));

            // borð-ar
            listVerbFinite.add(new MorphoRuleVerbFinite("[^a]ðar", 2, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));
            // skrif-ar
            listVerbFinite.add(new MorphoRuleVerbFinite("ar", 2, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));

            // mei-ddum
            listVerbFinite.add(new MorphoRuleVerbFinite("ddum", 4, IceMorphy.MorphoClass.VerbActive4, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // grei-dduð
            listVerbFinite.add(new MorphoRuleVerbFinite("dduð", 4, IceMorphy.MorphoClass.VerbActive4, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // breyt-tum
            listVerbFinite.add(new MorphoRuleVerbFinite("ttum", 3, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural ));
            // breyt-tuð
            listVerbFinite.add(new MorphoRuleVerbFinite("ttuð", 3, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // borð-uðum
            listVerbFinite.add(new MorphoRuleVerbFinite("uðum", 4, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular ));

            // borð-uðuð
            listVerbFinite.add(new MorphoRuleVerbFinite("uðuð", 4, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular ));

            // borð-uðu
            listVerbFinite.add(new MorphoRuleVerbFinite("uðu", 3, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular ));

            // borð-aðu
            listVerbFinite.add(new MorphoRuleVerbFinite("aðu", 3, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Imperative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // borð-aðir
            listVerbFinite.add(new MorphoRuleVerbFinite("aðir", 4, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // borð-aði
            listVerbFinite.add(new MorphoRuleVerbFinite("aði", 3, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

            // reyn-dum
            listVerbFinite.add(new MorphoRuleVerbFinite("dum", 3, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // þyng-dum
            listVerbFinite.add(new MorphoRuleVerbFinite("dum", 3, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // reyn-duð
            listVerbFinite.add(new MorphoRuleVerbFinite("duð", 3, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // þyng-duð
            listVerbFinite.add(new MorphoRuleVerbFinite("duð", 3, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // fyll-tuð
            listVerbFinite.add(new MorphoRuleVerbFinite("tuð", 3, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            listVerbFinite.add(new MorphoRuleVerbFinite("tuð", 3, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // breyt-um
            listVerbFinite.add(new MorphoRuleVerbFinite("tum", 2, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural ));
            // hir-tum
            listVerbFinite.add(new MorphoRuleVerbFinite("tum", 3, IceMorphy.MorphoClass.VerbActive6, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural ));

            // borð-um
            listVerbFinite.add(new MorphoRuleVerbFinite("ðum", 3, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular ));

            // greið-um
            listVerbFinite.add(new MorphoRuleVerbFinite("ðum", 3, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular ));

            // hætt-ir
             listVerbFinite.add(new MorphoRuleVerbFinite("ttir", 2, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));
            // breyt-tir
             listVerbFinite.add(new MorphoRuleVerbFinite("ttir", 3, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular ));

            // svar-arðu
            listVerbFinite.add(new MorphoRuleVerbFinite("arðu", 4, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));
            // heyr-irðu
            listVerbFinite.add(new MorphoRuleVerbFinite("irðu", 4, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));
            // met-urðu
            listVerbFinite.add(new MorphoRuleVerbFinite("urðu", 4, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // sen-dir
            listVerbFinite.add(new MorphoRuleVerbFinite("endir", 3, IceMorphy.MorphoClass.VerbActive3, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));

            // sen-tir
            listVerbFinite.add(new MorphoRuleVerbFinite("entir", 3, IceMorphy.MorphoClass.VerbActive3, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));

            // sen-di
            listVerbFinite.add(new MorphoRuleVerbFinite("endi", 2, IceMorphy.MorphoClass.VerbActive3, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // sen-ti
            listVerbFinite.add(new MorphoRuleVerbFinite("enti", 2, IceMorphy.MorphoClass.VerbActive3, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));


            // herð-ir
            listVerbFinite.add(new MorphoRuleVerbFinite("ðir", 2, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular ));
            // horf-ðir
            listVerbFinite.add(new MorphoRuleVerbFinite("ðir", 3, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // áger-ðist
            listVerbFinite.add(new MorphoRuleVerbFinite("ðist", 4, IceMorphy.MorphoClass.VerbMiddle1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Middle, IceTag.cPast, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));
            // áger-ist
            listVerbFinite.add(new MorphoRuleVerbFinite("ist", 3, IceMorphy.MorphoClass.VerbMiddle1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Middle, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));
            // áger-ast
            listVerbFinite.add(new MorphoRuleVerbFinite("ast", 3, IceMorphy.MorphoClass.VerbMiddle1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Middle, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cSingular));
            // -st
            listVerbFinite.add(new MorphoRuleVerbFinite("st", 2, IceMorphy.MorphoClass.VerbMiddle2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Middle, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

            // reyn-dir
            listVerbFinite.add(new MorphoRuleVerbFinite("dir", 3, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular ));
            // þyng-dir
            listVerbFinite.add(new MorphoRuleVerbFinite("dir", 3, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cSecondPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // breyt-tir
            listVerbFinite.add(new MorphoRuleVerbFinite("tir", 3, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));

            // grei-ddu
            listVerbFinite.add(new MorphoRuleVerbFinite("ddu", 3, IceMorphy.MorphoClass.VerbActive4, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // breyt-tu
            listVerbFinite.add(new MorphoRuleVerbFinite("ttu", 2, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // grei-ddi
            listVerbFinite.add(new MorphoRuleVerbFinite("ddi", 3, IceMorphy.MorphoClass.VerbActive4, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

            // breyt-ti
            listVerbFinite.add(new MorphoRuleVerbFinite("tti", 2, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

            // reyn-du
            listVerbFinite.add(new MorphoRuleVerbFinite("du", 2, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // fyll-tu
            listVerbFinite.add(new MorphoRuleVerbFinite("tu", 2, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // lif-ðu
            listVerbFinite.add(new MorphoRuleVerbFinite("ðu", 2, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // skrif-um
            listVerbFinite.add(new MorphoRuleVerbFinite("um", 2, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // ætl-iði
            listVerbFinite.add(new MorphoRuleVerbFinite("iði", 3, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // skrif-ið
            listVerbFinite.add(new MorphoRuleVerbFinite("ið", 2, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));
            // reyn-ið
            listVerbFinite.add(new MorphoRuleVerbFinite("ið", 2, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cSecondPerson, IceTag.cPlural, IceTag.cGenderUnspec, IceTag.cPlural));

            // reyn-di
            listVerbFinite.add(new MorphoRuleVerbFinite("di", 2, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));
            // þyng-di
            listVerbFinite.add(new MorphoRuleVerbFinite("di", 2, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));
            // ígrund-i
            listVerbFinite.add(new MorphoRuleVerbFinite("di", 1, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Subjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular ));

            // byrs-ti
            listVerbFinite.add(new MorphoRuleVerbFinite("ti", 2, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPast, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));
            // brey-ti
            listVerbFinite.add(new MorphoRuleVerbFinite("ti", 1, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));
            // -ti
            listVerbFinite.add(new MorphoRuleVerbFinite("ti", 2, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

            // grei-ði
            listVerbFinite.add(new MorphoRuleVerbFinite("ði", 1, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular ));

            // reyn-ir
            listVerbFinite.add(new MorphoRuleVerbFinite("ir", 2, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));
            // þyng-ir
            listVerbFinite.add(new MorphoRuleVerbFinite("ir", 2, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cSecondPerson, IceTag.cSingular));

            // legg, hegg
            listVerbFinite.add(new MorphoRuleVerbFinite("gg", 0, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular));

            // reyn-i
            listVerbFinite.add(new MorphoRuleVerbFinite("i", 1, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.IndicativeSubjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFirstPerson, IceTag.cSingular, IceTag.cGenderUnspec, IceTag.cSingular, true));
            listVerbFinite.add(new MorphoRuleVerbFinite("i", 1, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Subjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular, false));
            // stimpl-i
            listVerbFinite.add(new MorphoRuleVerbFinite("i", 1, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Subjunctive, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cThirdPerson, IceTag.cSingular, IceTag.cFirstPerson, IceTag.cSingular));

    }


    public void createVerbPastParticipleRules() {
            listVerbPastParticiple = new ArrayList<MorphoRuleVerbPastParticiple>();
            // gleym-dur
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("mdur", 3, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, false));
            // gleym-dar
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("mdar", 3, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, false));

            // fleng-dur
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("ngdur", 3, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, false));
            // fleng-dar
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("ngdar", 3, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, false ));

            // trygg-ður
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("ður", 3, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, false));

            // borð-aður
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("aður", 4, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cMasculine, IceTag.cSingular, IceTag.cNominative, false));
            // borð-aðir
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("aðir", 4, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cMasculine, IceTag.cPlural, IceTag.cNominative, false));
            // borð-aðar
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("aðar", 4, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFeminine, IceTag.cPlural, IceTag.cNominative, false));
            // borð-uð
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("uð", 2, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative, false, true));
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("uð", 2, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cPlural, IceTag.cNominative, false, false));
            // borð-að
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("að", 2, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true ));

            // flett
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("tt", 0, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // kúr-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("rt", 1, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // skemm-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("mt", 1, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // kenn-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("nt", 1, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // gláp-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("pt", 1, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // vanræk-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("kt", 1, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // smeyg-t
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("gt", 1, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cNeuter, IceTag.cSingular, IceTag.cNominative, true));
            // kenn-d
            listVerbPastParticiple.add(new MorphoRuleVerbPastParticiple("nd", 1, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Indicative, MorphoRuleVerb.Voice.Active, IceTag.cPresent, IceTag.cFeminine, IceTag.cSingular, IceTag.cNominative, false ));

    }

    public void createVerbRules() {

        listVerb = new ArrayList<MorphoRuleVerb>();

        // að þyngja - the infinitive
        listVerb.add(new MorphoRuleVerb("ja", 2, IceMorphy.MorphoClass.VerbActive5, MorphoRuleVerb.Mood.Infinitive, MorphoRuleVerb.Voice.Active, IceTag.cPresent ));

        // að borða - the infinitive
        listVerb.add(new MorphoRuleVerb("a", 1, IceMorphy.MorphoClass.VerbActive1, MorphoRuleVerb.Mood.Infinitive, MorphoRuleVerb.Voice.Active, IceTag.cPresent));

        // að reyna - the infinitive
        listVerb.add(new MorphoRuleVerb("a", 1, IceMorphy.MorphoClass.VerbActive2, MorphoRuleVerb.Mood.Infinitive, MorphoRuleVerb.Voice.Active, IceTag.cPresent));

        // að ágerast
        listVerb.add(new MorphoRuleVerb("ast", 3, IceMorphy.MorphoClass.VerbMiddle1, MorphoRuleVerb.Mood.Infinitive, MorphoRuleVerb.Voice.Middle, IceTag.cPresent));

    }
}


