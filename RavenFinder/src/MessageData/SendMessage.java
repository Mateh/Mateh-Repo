/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * The SendMessage class is the object that is sent from the client to the server, it contains the information needed to update chat logs and user locations.
 */
package MessageData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class SendMessage implements Serializable {

	private static final long serialVersionUID = 1L;
	private String msgType; // message type for handlers
	private double lats;// helpers latitude
	private double longs;// helpers longitude
	private String from;
	private String to;
	private String message;
	private String groupname;
	private ArrayList<String> groupList; //grouplist is used for the helper since it can have multiple groups
	private ConcurrentHashMap<String, String> groupmessages; 
	
	public SendMessage(String msg, String uname, String gname) {
		msgType = msg;
		from = uname;
		groupname = gname;
		groupmessages= new ConcurrentHashMap<String,String>();
	}

	public void setLoc(double la, double lo) {
		lats = la;
		longs = lo;
	}

	public void setTo(String t) {
		to = t;
	}

	public void setMessage(String m) {
		message = m;
	}

	public void setGroupname(String g) {
		groupname = g;
	}
	public void setGroupList(ArrayList<String> g)
	{
		groupList=g;
	}
	
	public void setGroupMsg(ConcurrentHashMap<String,String> t)
	{
		groupmessages=t;
	}
	public double getLat() {
		return lats;
	}

	public double getLong() {
		return longs;
	}

	public String getType() {
		return msgType;
	}

	public String getTo() {
		return to;
	}
	public String getFrom() {
		return from;
	}

	public String getMessage() {
		return message;
	}

	public String getGroupname() {
		return groupname;
	}
	public ArrayList<String> getGroupList()
	{
		return groupList;
	}
	
	public ConcurrentHashMap<String,String> getGroupMsg()
	{
		return groupmessages;
	}
}
