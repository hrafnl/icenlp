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
package is.iclt.icenlp.core.iceNER;
import java.io.*;
%% NameScanner
%public
%class NameScanner
%standalone
%unicode

%{
      java.io.Writer out = new BufferedWriter(new OutputStreamWriter(System.out));

      public void parse(java.io.Writer _out) throws java.io.IOException
      {
        out = _out;
        while (!zzAtEOF)
            yylex();
      }

	
%}

%eof{
        try {
          out.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
%eof}

//WhiteSpace = [ ]
WhiteSpace =  [ \t\n\r]+

Upper = [A-ZÁÉÐÍÓÚÝÞÆÖ]
Lower = [a-záéðíóúýþæö]
Char = {Upper}|{Lower}
Word = {Char}+
WebPage = (http:\/\/)?(www)?({Char}+\.)+{Char}{Char}{Char}?
Email = [a-zA-Z0-9]+@[a-zA-Z0-9]+.[a-z]{2,6}
Digit = [0-9]
Time = ((0?[1-9]|1[012])(:[0-5]{Digit}){0,2}(\ [AP]M))|([01]{Digit}|2[0-3])(:[0-5]{Digit}){0,2}
Date = ((((0?[1-9]|[12]{Digit}|3[01])[\.\-\/](0?[13578]|1[02])[\.\-\/]((1[6-9]|[2-9]{Digit})?{Digit}{2}))|((0?[1-9]|[12]{Digit}|30)[\.\-\/](0?[13456789]|1[012])[\.\-\/]((1[6-9]|[2-9]{Digit})?{Digit}{2}))|((0?[1-9]|1{Digit}|2[0-8])[\.\-\/]0?2[\.\-\/]((1[6-9]|[2-9]{Digit})?{Digit}{2}))|(29[\.\-\/]0?2[\.\-\/]((1[6-9]|[2-9]{Digit})?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00)))|(((0[1-9]|[12]{Digit}|3[01])(0[13578]|1[02])((1[6-9]|[2-9]{Digit})?{Digit}{2}))|((0[1-9]|[12]{Digit}|30)(0[13456789]|1[012])((1[6-9]|[2-9]{Digit})?{Digit}{2}))|((0[1-9]|1{Digit}|2[0-8])02((1[6-9]|[2-9]{Digit})?{Digit}{2}))|(2902((1[6-9]|[2-9]{Digit})?(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00)|00))))
Number = {Digit}*[\.|,]{Digit}+|{Digit}+
Degree = {Number}°
Temp = {Degree}[Cc]|[Ff]
Percentage = {Number}%
Abbreviation = {Char}\.|{Lower}\.({Lower}\.)+|({Lower}\.)+{Lower}+\.

Person = ((({Upper}{Lower}+){WhiteSpace})+({Upper}\.?{WhiteSpace})?{Upper}{Lower}+(son(ar)?|syni|sen|dótt[iua]r))|({Upper}{Lower}+{WhiteSpace}{Upper}\.{WhiteSpace}{Upper}{Lower}+)

Company = ((({Upper}{Lower}+){WhiteSpace})+({Word}{WhiteSpace}){0,2}(hf|sf|ehf|inc|ltd|Group)\.?)|{Char}({Char}|{Digit})*{Digit}({Char}|{Digit})*|{Digit}({Char}|{Digit})*{Char}({Char}|{Digit})*|(({Upper}{Lower}+{WhiteSpace})*{Upper}+{Lower}*-?(bank(i(nn)?|a(n(n|um|s)?)?)|bandalag(i(ð|nu)?|s(ins)?)?|blað(s|i(ð|nu)?|sins)?|bíó(i(ð|nu)?|s(ins)?)?|deildin(a|ni)?|deildarinnar|dóm(ur|i|s)?|eftirlit(i(ð|nu)?|s(ins)?)?|félag(i|s|ið|nu|sins)?|flokk(i|[ua]r|urinn|inn|num|s(ins)?)?|fylking(u|ar(innar)?|in|una|unni)|gæsl(a(n)?|u(n(a|n(i|ar))))|hús(i(ð|nu)?|(s(ins)?)?)?|ótel(i|s)?|ráð(i(ð|nu)?|(s(ins)?)?)?|ráðuneyti(ð|nu|s(ins)?)?|sala(nn)?|sölu(n(a|ni|nar))?|samband(i(ð|nu)?|s(ins)?)?|samtök(um|in|unum)?|samtaka(nna)?|sjóð(ur(inn)?|i(nn)?|inum|s(ins)?)?|skól(i(nn)?|a(n(n|um|s)?)?)|spítal(i(nn)?|a(n(n|um|s))?)|stofa(nn)?|stofu(n(a|ni|nar))?|stofnun(in(a)?|ni|ar)?|stofnuni(in|na|nni|nnar)|[Ss]töð(in(a|ni)|var)?|[Ss]töðvarinnar|veit(a(n)?|u(na)?|unni|unnar)|þjónust(a(n)?|u(na)?|unni|unnar))({WhiteSpace}{Upper}{Lower}+)*)

Event = {Upper}{Lower}*-?(hátíð(ar)?|keppni(n)?|leik(a(r)?|um)|mót(i|s)?|sýning(u|ar)?|maraþon(i|s)?|hlaup(i|s)?)

Location = ({Upper}{Lower}+(ás[is]?|bakk[ai]|bjarg[is]|braut(ar)?|brekk[au]|búð(a|ir|um)|borg(ar)?|bæ(r|i|jar)?|dal(ur|i(r)?|a|s)?|dölum|ey(ju|ju)?|eyj(a|ar|u)|eyr(i|ar)|fell(i|s)?|fjall(i|s)?|fj[öa]rð(u(r|m)|a(r)?)?|firði(r)?|fljót(i|s)?|fló[ai]|foss(i)?|garða(r)?|görðum|gata|götu|gerði(s)?|gil(s)?|gljúf(ur(s)?|ri)|grund(ar)?|haf(i|s)?|háls(i)?|heið(i|ar)|heim(a(r)?|um)|helli(r|s)?|holt(i|s)?|hól(i|l|s)|hólm(i|s|ur)|[Hh]raun(i|s)?|hvamm(i|s|ur)?|höfð[ia]|höfn|hafnar|kot(i|s)?|kjördæmi(s(ins)?|ð|nu)?|land(i|s)?|lind(ar)?|læk(jar|ur)?|mið(a|um)|mó(ar|um|a)|mýr(ar|i)|mörk|markar|merkur|nes(i|s)?|núp(i|ur|s)?|ós(i|s)?|rík(i(s|n)?|j(unum|anna))|sand(i|s|ur)?|skarð(i|s)?|slóð(ar)?|stað(i(r)?|a)?|stöðum|stæti(s)?|sund(i|s)?|tang[ia]|tind(i|s|ur)?|tjörn|tjarnar|torg(i|s)?|tún(i|s)?|vatn(i|s)?|veg(ur|i|s)?|velli(r)?|vallar|vík([au]r)?|vog(ur|i|s)?|völl(ur)?))

PersonRelation = [Aa]f[ia]|[Aa]mma|[Öö]mmu|[Bb]arn(a|i|s)?|[Bb]örn(um)?|[Bb]róð[iu]r|[Bb]ræð(ur|rum|ra)|[Dd]ótt[iu]r|[Dd]æt(ur|rum|ra)|[Ee]iginkon[au]|[Ee]iginma(ður|nn(i|s)?)|[Ff]aðir|[Ff]öður|[Ff]rænd(i|a|u(r|m))|[Ff]rænk(a|u(r|m)?)|[Ff]oreldr(i(s)?|um|a)|[Kk]ær[au]st(a(n(n|um|s)?)?|u(n(a|n(i|ar)))?|i(nn)?)|[Mm]amma|[Mm]ömmu|[Mm]óð[iu]r|[Nn]ágr(ann(i|a(r)?)|önnum)|[Pp]abb[ia]|[Ss]on(a(r)?|u(r|m))?|[Ss]yni(r)?|[Ss]yst([iu]r|r(um|a))|[Vv]ink(on(a|u(r|m)?)|venna)|[Vv]in(i(r)?|u(r|m)|a(r)?)?|[Ææ]tting(i|j(a(r)?|um))
	
PersonRole = {Upper}?{Lower}*([Bb]loggar(i(nn)?|a(n(n|um|s))?)|[Dd]ómar(i(nn)?|a(n(n(a)?|um|s)?)?)|[Dd]rottning(u(n(a|ni))?|a(r(innar)?)|in)?|[Ee]igand(i(nn)?|a(n(n(a)?|um|s)?)?)|[Ee]igend(u(r(nir|na)?|m|num)|a)|[Ff]élag(i(nn)?|a(n(n(a)?|um|s)?)?)|[Ff]járfesti(s(ins)?|r(inn)?|n(n|um)?)?|[Ff]járfest(a(r(nir)?|n(n)?a)?|u(nu)?m)?|[Ff]oring(i(nn)?|ja(n(n|um|s))?)|[Ff]orset(a(n(n|s|um))?|i(nn)?)|[Ff]rambjóðand(i(nn)?|a(n(n|um|s))?)|[Ff]ræðing(i(nn|num)?|s(ins)?|ur(inn)?|a(r(nir)?|n(n)?a)?|u(nu)?m)?|[Ff]ulltrú(i(nn)?|a(n((n)?a|n|um|s)?|r(nir)?)?|u(nu)?m)|[Ff]yrirsæt(a(n)?|u(n(a|ni|nar))?)|[Gg]arp(ur(inn)?|i(n(n|um))?|s(ins)?)?|[Gg]jaf(i(nn)?|a(n((n(a)?|a)|um|s)|r(nir)?)?)|[Gg]jöfu((nu)?m)|herj(i(nn)?|a(n(a|n(a)?|um|s)?|r(nir)?)?|u(nu)?m)|herr(i(nn)?|a(n(n|um|s)?)?)|[Hh]öfund(ur(inn)?|ar(ins)?|i(n(n|um))?)?|[Kk]app(i(nn)?|a(n(n|um|s)?)?)|[Kk]ennar(i(nn)?|a(n(n|um|s)?)?)|[Kk]on(a(n)?|u(n(a|ni|nar))?)|[Kk]óng(i(n(n|um))?|s(ins)?|ur(inn)?)?|[Ll]eiðtog(i(nn)?|a(n(n|um|s))?)|[Ll]eikar(i(nn)?|a(n(n|um|s))?)|lið(i(nn)?|a(n(n|um|s))?)|[Ll]ækni(r(inn)?|n(n|um)|s(ins)?)?|[Mm]aður(inn)?|[Mm]ann((i(n(n|um))?)|s(ins)?|a(nna)?)?|[Mm]enn(i(rnir|na))?|[Mm]önnu(nu)?m|mæring(ur(inn)?|i(nn)?|s(ins)?|num)?|[Mm]eistar(i(nn)?|a(n(s|n|um))?)|[Nn]em(i(nn)?|a(n(n|um|s)?)?)|[Pp]rins(i(n(n|um|s))?)?|[Pp]rinsess(a(n)?|u(n(a|ni|nar))?)|[Pp]rófessor(inn|s(ins)?|num)?|stjór(i(nn)?|a(n((n)?a|n|um|s)?|r(nir)?)?|u(nu)?m)|[Ss]tofnand(i(nn)?|a(n(a|n(a)?|um|s)?)?)|[Ss]öngvar(i(nn)?|a(n(n|um|s))?)|[Úú]tsendar[ia]|virk(i(nn)?|ja(n(n|um|s)?)?)|[Þþ]jálfar(i(nn)?|a(n(n|um|s))?)|þór(i(n(n|um))?|s(ins)?)?|þeg(i(nn)?|a(n((n)?a|n|um|s)?|r(nir)?)?|u(nu)?m)|[Aa]lban(inn|an(n|um|s))|Belg(inn|an(n|um|s))|Bret(inn|an(n|um|s))|Búlgar(inn|an(n|um|s))|Dan(inn|an(n|um|s))|lending(urinn|inn|num|sins)|Finn(inn|an(n|um|s))|Frakk(inn|an(n|um|s))|Grikk(inn|jan(n|um|s))|Ítal(inn|an(n|um|s))|Ír(inn|an(n|um|s))|Japan(inn|an(n|um|s))|Króat(inn|an(n|um|s))|Lett(inn|an(n|um|s))|Lithá(inn|an(n|um|s))|Portúgal(inn|an(n|um|s))|Rúmen(inn|an(n|um|s))|Rúss(inn|an(n|um|s))|Serb(inn|an(n|um|s))|Slóvak(inn|an(n|um|s))|Slóven(inn|an(n|um|s))|Skot(inn|an(n|um|s))|Sví(inn|an(n|um|s))|Tékk(inn|an(n|um|s))|Tyrk(inn|jan(n|um|s))|kan(inn|an(n|um|s))|verj(inn|an(n|um|s)))

CompanyRole = {Upper}?{Lower}*-?([Bb]ank(i(nn)?|a(n(n|um|s)?)?)|[Bb]lað(s|i(ð|nu)?|sins)?|[Bb]andalag(i(ð|nu)?|s(ins)?)?|[Bb]íó(i(ð|nu)?|s(ins)?)?|[Dd]eild(ar(innar)?|in(a|ni)?)?|[Ee]ftirlit(i(ð|nu)?|s(ins)?)?|[Ff]angelsi(s(ins)?|nu|ð)?|[Ff]élag(i(ð|nu)?|s(ins)?)?|[Ff]lokk(i|[ua]r|urinn|inn|num|s(ins)?)?|framleiðand(i(nn)?|a(n(n|um|s))?)|[Ff]ylking(u|ar(innar)?|in|una|unni)?|[Ff]yrirtæki(s(ins)?|ð|nu)?|[Hh]eimili((ð|nu)?|s(ins)?)?|[Hh]ljómsveit(ar(innar)?|in(a|ni)?)?|[Hh]ús(i(ð|nu)?|(s(ins)?)?)?|[Hh]ótel(i|s)?|keðj(a(n)?|u(n(a|ni|nar))?)|leig(an|un(a|ni|nar))|[Ll]ist(i(nn)?|a(n(s|n|um))?)|[Rr]áðuneyti(ð|nu|s(ins)?)?|[Ss]ala(nn)?|[Ss]ölu(n(a|ni|nar))?|[Ss]amband(i(ð|nu)?|s(ins)?)?|[Ss]amtök(um|in|unum)?|[Ss]amtaka(nna)?|[Ss]afn(i(ð|nu)?|s(ins)?)?|[Ss]jóð(ur(inn)?|i(nn)?|inum|s(ins)?)?|[Ss]kól(i(nn)?|a(n(n|um|s)?)?)|[Ss]pítal(i(nn)?|a(n(n|um|s))?)|[Ss]tjórn(ar(innar)?|in(a|ni)?)?|[Ss]tofa(nn)?|[Ss]tofu(n(a|ni|nar))?|[Ss]tofnun(in(a)?|ni|ar)?|[Ss]tofnuni(in|na|nni|nnar)|[Ss]töð(in(a|ni)?|var)?|[Ss]töðvarinnar|[Vv]ef(ur(inn)?|i(nn)?|s(ins)?)?|[Vv]erslun(ar(innar)?|in(a|ni)?)?|[Þþ]jónust(a(n)?|u(na)?|unni|unnar)|[Þþ]ing(i(ð|nu|s)?|s(ins)?)?)

EventRole =	{Upper}?{Lower}*-?(hátíð(ar(innar)?|in(a|ni)?)?|keppni(n(a|n(i|ar))?)?|leik(a(r(nir)?|n(n)?a)?|(un)?um)|mót(i(ð|nu)?|s(ins)?)?|sýning(u(na|nni)?|ar(innar)?|in)?|maraþon(i(ð|nu)?|s(ins)?)?|hlaup(i(ð|nu)?|s(ins)?)?)

LocationRole = {Upper}?{Lower}*-?([Nn]orður|[Ss]uður|[Aa]ustur|[Vv]estur|[Bb]org(ar(innar)?|in(a|ni)?)?|[Bb]æ(r(inn)?|jar(ins)?|inn|num)?|[Dd]al(ur(inn)?|num|inn|s(ins)?)?|[Ee]y(ju(num|nni|m)?|na|ja(r(innar|nar)?|nn)?|in)?|[Ff]ell(i(ð|nu)?|s(ins)?)?|[Ff]jall(i(ð|nu)?|s(ins)?)?|[Ff]jörð(ur(inn)?|inn)?|[Ff]irði(num)?|[Ff]jarðarins|[Ff]ljót(i(ð|nu)?|s(ins)?)?|[Ff]ló(i(nn)?|a(nn|num|ns)?)|[Ff]oss(i(nn|num|ns)?)?|[Gg]ata(n)?|[Gg]ötu(na|nni|nnar)?|[Hh]eið(i(n(a|ni)?)?|ar(innar)?)|[Hh]raun(i(ð|nu)?|s(ins)?)?|höfn(in(a|ni)?)?|hafnar(innar)?|[Hh]öll(in(a|ni)?)?|[Hh]allar(innar)?|land(i(ð|nu)?|s(ins)?)?|nes(i(ð|nu)?|s(ins)?)?|ríki((ð|nu)?|s(ins)?)?|[Ss]kag(i(nn)?|a(n(n|s|um))?)|[Ss]kál(i(nn)?|a(n(n|s|um))?)|[Ss]lóð(ar(innar)?|in(a|ni)?)?|stað([au]r)?|[Ss]træti(ð|nu|s(ins)?)?|[Ss]trönd(in(a|ni)?|um)?|[Ss]trandar(innar)?|[Ss]væði(ð|nu|s(ins)?)?|[Tt]org(i(ð|nu)?|s(ins)?)?|[Vv]atn(i(ð|nu)?|s(ins)?)?|[Vv]eg(ur(inn)?|i(n(n|um)?)?|ar(ins)?)?|[Vv]öll(ur(inn)?|inn)?|[Vv]elli(num)?|[Vv]allar(ins)?|[Þþ]orp(i(ð|nu)?|s(ins)?)?|[Öö]ræf(i|um|a))
	
%%
{Event} { out.write(yytext()+" SEP EVENT\n");}
{Person} { out.write(yytext()+" SEP PERSON\n");}
{Company} { out.write(yytext()+" SEP COMPANY\n");}
{Location} { out.write(yytext()+" SEP LOCATION\n");}
{EventRole} { out.write(yytext()+" SEP ROLE_EVENT\n");}
{PersonRole} { out.write(yytext()+" SEP ROLE_PERSON\n");}
{CompanyRole} { out.write(yytext()+" SEP ROLE_COMPANY\n");}
{LocationRole} { out.write(yytext()+" SEP ROLE_LOCATION\n");}
{PersonRelation} { out.write(yytext()+" SEP RELATION_PERSON\n");}
{WhiteSpace} {;}
{Word} {;}
{WebPage} {;}
{Email} {;}
{Time} {;}
{Date} {;}
{Number} {;}
{Degree} {;}
{Temp} {;}
{Percentage} {;}
{Abbreviation} {;}
. {;}
