package is.ru.icenlpserver.common;

/**
 * Generic pair class.
 * @author hlynurs
 *
 * @param <l> Type of the left object.
 * @param <r> Type of the right object.
 */
public class Pair<l, r>
{
	// Member variables.
	public l one;
	public r two;
	
	// Constructor for the class. 
	public Pair(l one, r two)
	{
		this.one = one;
		this.two = two;
	}
}
