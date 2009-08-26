/*
 * Word.java
 *
 * Created on 27. mars 2008, 17:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package is.iclt.icenlp.core.lemmald.icenlp;

/*
 * Word.java
 *
 * Created on 14. j�l� 2007, 17:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
public class Word {
	
	private String word = null;
	private String tag = null;
        private boolean unknown = false;
	
	public Word( String word, String tag, boolean unknown ){
		this.word = word;
		this.tag = tag;
                this.unknown = unknown;
	}
	
	public String getWord(){
		return word;
	}

	public String getTag(){
		return tag;
	}
        
        public boolean isUnknown(){
                return unknown;
        }

	public boolean wordEquals( String otherWord ){
		return word.toLowerCase().equals(otherWord.toLowerCase());
	}
	
}
