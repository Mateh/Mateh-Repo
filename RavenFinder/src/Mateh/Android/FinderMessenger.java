/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * This FinderMessenger class is used to send a message and receive a message. Messages can be made in the constructor and then sent to the server.
 * The class creates a connection, sends the message, waits for response, then finishes.
 */

package Mateh.Android;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.util.Log;

import MessageData.RecMessage;
import MessageData.SendMessage;
//this messenger class is used by the Finder_Main class
public class FinderMessenger implements Runnable{

	private Socket s;
	private SendMessage msg;
	private String msgType;
	private Finder_Main client;
	
	public FinderMessenger(String mType, Finder_Main c, SendMessage ms)
	{
		msgType=mType;
		client=c;
		msg=ms;
	}
		
	//client sends message out to server
	private void sendMsg(SendMessage amsg) throws ConnectException,OptionalDataException, ClassNotFoundException, IOException
	{
		s = new Socket(MenuActivity.HOST, MenuActivity.HOSTPORT);
		ObjectOutputStream out;
		out = new ObjectOutputStream(s.getOutputStream());		
		out.writeObject(amsg);	
	}
	//client receives message from server
	private void recMsg(Socket s) throws OptionalDataException, ClassNotFoundException, IOException
	{
		ObjectInputStream in=new ObjectInputStream(s.getInputStream());
		RecMessage rmsg= (RecMessage)in.readObject(); //deserialize object from socket stream
		s.close();
		handleResponse(rmsg);
		
	}
	
	//method that creates an update message to ask for an update of the group information
	public void updateGroupMessage() throws OptionalDataException, ClassNotFoundException, IOException
	{
		msg=new SendMessage("FUPDATE", MenuActivity.username, MenuActivity.groupName);
		sendMsg(msg);
	}
	
	//handle response messages sent from the server
	public synchronized void handleResponse(RecMessage m)
	{
		if(m.getType().equals("FUPDATE"))
		{
			Finder_Main.clientHelperList= m.getHList();
			
			//method used to update the UI on the Finder_Main
			client.runOnUiThread(new Runnable() {
	    	    public void run() {
	    	    	client.updateGroup();
	    	    }
	    	});
			 //calls the method in the Finder_Main to update the list of helpers
			MenuActivity.updateChat(m.getMList());
		}
	}

	public void run() {

		try {
		//based on the constructor setting the message, follow if or else	
		if(msgType.equals("FUPDATE"))
		{
			updateGroupMessage();
		}
		else if(msgType.equals("MESSAGE"))
		{
			sendMsg(msg);
		}
		//after message is sent, call method so that client can wait to receive response
			recMsg(s);
		} 
		catch(ConnectException e)
		{	//if connection is not made, or is broken, then stop updates and client needs to manually reset the update intervals
			Log.e("NETWORK", "Connection can't be made to server.");
			client.stopUpdates();
		}
		catch(NullPointerException e)
		{
			Log.e("NETWORK", "Connection disconnected and cannot receive message");
			client.stopUpdates();
		}
		catch (OptionalDataException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	
	
}

