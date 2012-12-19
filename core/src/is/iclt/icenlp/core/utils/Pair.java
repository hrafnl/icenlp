package is.iclt.icenlp.core.utils;
public class Pair<l, r>
{
	// Member variables.
	public l one;
	public r two;

    // Default constructor
    public Pair()
    {
        this.one = null;
        this.two = null;
    }
		
	// Constructor for the class. 
	public Pair(l one, r two)
	{
		this.one = one;
		this.two = two;
	}
}

