/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * The RecMessage class is the object that is sent from the server to the client, it contains the information needed to update chat logs and user locations.
 */

package MessageData;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class RecMessage implements Serializable{

	private static final long serialVersionUID = 1L;
	private String msgType; //message type for handlers
	private ConcurrentHashMap<String,HelperInfo> helperList;//the helper list from the server
	private ArrayList<ChatMessage> messageList;//list of messages from helpers or finders
		public RecMessage(String msg)
	{
	 msgType=msg;
	}
	
		public String getType()
		{
			return msgType;
		}
		public ConcurrentHashMap<String,HelperInfo> getHList()
		{
			return helperList;
		}
		public ArrayList<ChatMessage> getMList()
		{
			return messageList;
		}
		public void setHList(ConcurrentHashMap<String,HelperInfo> hlist)
		{
			helperList=hlist;
		}
		public void setMList(ArrayList<ChatMessage> mlist)
		{
			messageList=mlist;
		}
		
}
