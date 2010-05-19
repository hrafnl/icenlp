package is.iclt.icenlp.icetagger;

import java.util.List;

import is.iclt.icenlp.common.configuration.Configuration;
import is.iclt.icenlp.core.utils.Word;

public class TestRunner {
	public static void main(String[] args) 
	{
		if (!Configuration.loadConfig("/home/hlynur/temp/icenlp/server/configs/server.conf"))
		{
			System.exit(0);
            return;
        }
		
		try {
			IceTagger tagg = IceTagger.instance();
			tagg.lemmatize(true);
			List<Word> words = tagg.tag("Í dag er gaman");
			
			for(Word w : words){
				System.out.println(w.getLexeme() + " " + w.getTag() + " " + w.getLemma());
			}
			
			
		} catch (IceTaggerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
