/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * The helper info class is a serializable class that is used to store the information of a helper, this is used on the server as well.
 */
package MessageData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class HelperInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private double lats;
	private double longs;
	private ArrayList<String> groupname;
	private ConcurrentHashMap<String,String> info;
	
	public HelperInfo(String n, double la, double lo, ArrayList<String> g, ConcurrentHashMap<String, String> in)
	{
		name=n;
		lats=la;
		longs=lo;
		groupname=g;
		info=in;
	}
	public String getName()
	{
		return name;
	}
	public double getLat()
	{
		return lats;
	}
	public double getLongs()
	{
		return longs;
	}
	public ArrayList<String> getGroupname()
	{
		return groupname;
	}
	public ConcurrentHashMap<String,String> getInfo()
	{
		return info;
	}
}
