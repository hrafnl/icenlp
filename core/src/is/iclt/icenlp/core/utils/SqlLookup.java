package is.iclt.icenlp.core.utils;

import java.sql.*;

/**
 * Created by IntelliJ IDEA.
 * User: gudmundur
 * Date: 3/12/12
 * Time: 9:40 AM
 * To change this template use File | Settings | File Templates.
 */
public class SqlLookup
{

	public static String[] sqlWordLookup(String lookUpWord, String url_in, String user_in, String password_in)
	{
		String[] possibleTags = new String[6];

		String underscoredLookUpWord = icelandicCharToUnderscore(lookUpWord);

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

//		String url = "jdbc:mysql://localhost:3306/testdb";
//		String user = "root";
//		String password = "adgangur";
		String url = url_in;
		String user = user_in;
		String password = password_in;

		try
		{
//Class.forName("com.mysql.jdbc.Driver").newInstance();
			con = DriverManager.getConnection(url_in, user_in, password_in);
			if(!con.isClosed())
			{
//      			System.out.println("Successfully connected to MySQL server using TCP/IP...");
			}
			st = con.createStatement();

			st.executeUpdate("USE testdb;");
			st.executeUpdate("SET NAMES 'utf8';");

			rs = st.executeQuery("select * from wordlist where word like '"+underscoredLookUpWord+"'");
			possibleTags = writeResultSet(rs,lookUpWord);

			st.close();
		}
		// catch (Exception ex)
		catch (SQLException ex)
		{
			System.err.println("Exception: " + ex.getMessage());
		  //  Logger lgr = Logger.getLogger(Version.class.getName());
		  //  lgr.log(Level.SEVERE, ex.getMessage(), ex);

		}
		finally
		{
			try {
				if (rs != null)
				{
					rs.close();
				}
				if (st != null)
				{
					st.close();
				}
				if (con != null)
				{
					con.close();
				}

			}
			catch (SQLException ex)
			{
			  //      Logger lgr = Logger.getLogger(Version.class.getName());
				//    lgr.log(Level.WARNING, ex.getMessage(), ex);
			}
       	}

		return possibleTags;
	}

	private static String icelandicCharToUnderscore(String in)
	{
		in = in.replace('á','_');
		in = in.replace('é','_');
		in = in.replace('í','_');
		in = in.replace('ú','_');
		in = in.replace('ý','_');
		in = in.replace('ó','_');
		in = in.replace('ú','_');
		in = in.replace('æ','_');
		in = in.replace('ð','_');
		in = in.replace('þ','_');

		return in;
	}

	// takes in the set of results and filters out the wrong matches
	// returns an array where : array[0] = Adj., array[1] = nom. case, array[2] = acc. case, array[3] = dat, array[4] = gen, array[5] = other
	private static String[] writeResultSet(ResultSet resultSet, String matchWord) throws SQLException
	{
		String[] out = {"","","","","",""}; // initiate string array without null to avoid problems later on

//		System.out.println("SQL matchWord=("+matchWord+")");

		// ResultSet is initially before the first data set
		while (resultSet.next())
		{
			// It is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g. resultSet.getSTring(2);
			String word = resultSet.getString("word");
			String beintandlag = resultSet.getString("beintandlag");
			if (word.equals(matchWord))
			{
//				System.out.println("word:" + word + "\tbeintandlag:" + beintandlag);

				if (beintandlag.equals("ATV"))
				{
					out[0] = "atv";
				}
				if (beintandlag.equals("NF"))
				{
					out[1] = "n";
				}
				if (beintandlag.equals("ÞF"))
				{
					out[2] = "o";
				}
				else if (beintandlag.equals("ÞGF"))
				{
					out[3] = "þ";
				}
				else if (beintandlag.equals("EF"))
				{
					out[4] = "e";
				}
				else
				{
					out[5] = beintandlag;
				}
			}
		}
		return out;
	}
}
