/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * This HelperMessenger class is used to send a message and receive a message. Messages can be made in the constructor and then sent to the server.
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

import MessageData.RecMessage;
import MessageData.SendMessage;
import android.util.Log;
//this messenger class is used for the helper
public class HelperMessenger implements Runnable{
	
	
	private Socket s;
	private SendMessage msg;
	private String msgType;
	private Helper_Main client;
	
		public HelperMessenger(String mType, Helper_Main c, SendMessage ms)
	{
	msgType=mType;
	client=c;
	msg=ms;
	}
		
	//client connection and message delivery, sends out the message passed in the parameters
	private void sendMsg(SendMessage amsg) throws ConnectException, OptionalDataException, ClassNotFoundException, IOException
	{
			s = new Socket(MenuActivity.HOST, MenuActivity.HOSTPORT);
			ObjectOutputStream out;	
			out = new ObjectOutputStream(s.getOutputStream());		
			out.writeObject(amsg);
			//System.out.println("Message sent");		
	}
	
	//method that waits on a response message from the server
	private void recMsg(Socket s) throws OptionalDataException, ClassNotFoundException, IOException
	{
		ObjectInputStream in=new ObjectInputStream(s.getInputStream());
		RecMessage rmsg= (RecMessage)in.readObject(); //deserialize object from socket stream
		s.close();
		handleResponse(rmsg);
	}
	
	//messages that can be sent from client to update server on the GPS coordinates
	public void updateServer() throws OptionalDataException, ClassNotFoundException, IOException
	{
		sendMsg(msg);
	}
	
	//handle messages sent from server
	public void handleResponse(RecMessage m)
	{
		if(m.getType().equals("HUPDATE"))
		{	//update chat messages
			MenuActivity.updateChat(m.getMList());
		}
	}

	public void run() {
		try {
		if(msgType.equals("HUPDATE"))
		{
				updateServer();
		}
		else if(msgType.equals("MESSAGE"))
		{
			sendMsg(msg);
		}
		//after message is sent, call recMsg() so that client can wait to receive response
			recMsg(s);
		} 
		catch(ConnectException e)
		{	//stopping updates if network connection failed
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

