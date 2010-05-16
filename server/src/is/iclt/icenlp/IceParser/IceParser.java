package is.iclt.icenlp.IceParser;

import is.iclt.icenlp.facade.IceParserFacade;

import java.io.IOException;


public class IceParser implements IIceParser{

    // Private static variable that contains the singleton
    // instance of this class.
    private static IceParser instance_ = null;

    // Private member variable that holds an instance of the
    // IceParser facade.
    private IceParserFacade parser;
    
    public synchronized static IceParser instance(){
        if(instance_ == null)
            instance_ = new IceParser();
        return instance_;
    }
    
    protected IceParser(){
        System.out.println("[i] IceParser instance created.");
        parser = new IceParserFacade();
    }

    public String parse(String text) {
        boolean include_functions = true;
        boolean phrase_per_line = true;
        try {
            return parser.parse(text, include_functions, phrase_per_line);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }
}
