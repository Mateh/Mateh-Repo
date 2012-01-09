/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * The ChatMessage class is a class that holds the information of a chat message from one client to another.
 */

package MessageData;

import java.io.Serializable;

//message class that holds the body of a chat message and the sender
public class ChatMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	private String header;
	private String body;
	public ChatMessage(String aheader, String abody){
		
		header=aheader;
		body=abody;
	}
	
	public String getHeader()
	{
		return header;
	}
	public String getBody()
	{
		return body;
	}
	
	public void setHeader(String s)
	{
		header=s;
	}
	public void setBody(String s)
	{
		body=s;
	}
}
