/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * This is the RavenFinderServer, it is the server that stores and distributes messages between clients using the RavenFinder application running on Android mobile devices.
 * It must be running and the according IP/port must be set in the client code base so that they can connect to the server. The server itself essentially runs and waits for incoming messages and creates a 
 * thread to handle the message. Handling the message always results in a response message to the client.
 */

package Mateh.Android;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import MessageData.HelperInfo;
import MessageData.RecMessage;
import MessageData.SendMessage;
import MessageData.ChatMessage;

public class RavenFinderServer implements Runnable {
	
	ServerSocket listener;
	int server_port;
	ExecutorService threadExecutor;
	public final int maxthreads=100;
	public final int port= 15000;//port of the server
	
	public ConcurrentHashMap<String, HelperInfo> helperList; //hash map of helpers being supported
	public ConcurrentHashMap<String, ArrayList<ChatMessage>> ChatMessageList;//hash map of chat messages waiting to be sent to the acoording clients

	public RavenFinderServer(){
		
		helperList= new ConcurrentHashMap<String, HelperInfo>();
		ChatMessageList= new ConcurrentHashMap<String, ArrayList<ChatMessage>>();
		
		
	try {
		threadExecutor = Executors.newFixedThreadPool( maxthreads );
		listener = new ServerSocket(port);
		System.out.println("IP: " + InetAddress.getLocalHost() + " PORT: "+ port);
	} catch (IOException e) {
		e.printStackTrace();
	}	
}
//run method for thread, accepts incoming connections and begins handling requests from client
public void run()
{
	while(true)
	{
		try {
			//accepting any incoming connections from clients
			final Socket s = listener.accept();
		
			System.out.println("Server Accepted a connection from ip: " + s.getInetAddress() + " Port: " + s.getPort());
			//using threadExecutor to create new thread and run handlerequest
			threadExecutor.execute(new Runnable(){
							public void run(){
								try {
									handleRequest(s);
								} catch (Exception e) {
										e.printStackTrace();
								}
							}}); 
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
	
//handling the different messages
	
	public void handleRequest(Socket s) 
	{
		try
		{
			ObjectInputStream in=new ObjectInputStream(s.getInputStream());
			SendMessage msg= (SendMessage)in.readObject(); //deserialize object from socket stream
			
			System.out.println("got request from " + s.getRemoteSocketAddress().toString() + " Message= "+ msg.getType());
			//add user to list for messaging if they aren't already there
			if(ChatMessageList.isEmpty())
			{
				ArrayList<ChatMessage> a= new ArrayList<ChatMessage>();
				ChatMessageList.put(msg.getFrom(), a);
			}
			else if(!ChatMessageList.containsKey(msg.getFrom()))
			{
				ArrayList<ChatMessage> a= new ArrayList<ChatMessage>();
				ChatMessageList.put(msg.getFrom(), a);
			}
			
			//in.close();
			//specific message handling
			if(msg.getType().equals("FUPDATE"))
			{
				handleUpdate(s,msg);
			}
			else if(msg.getType().equals("HUPDATE"))
			{
				handleHelperUpdate(s,msg);
			}
			else if(msg.getType().equals("MESSAGE"))
			{
				handleChatMessage(s,msg);
			}
		}
		catch(SocketException e)
		{
			System.out.println("Client has disconnected");
			//s.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			
		}
	}
	
	//the update handler for a message sent from the finder
	public void handleUpdate(Socket s, SendMessage m) throws OptionalDataException, ClassNotFoundException, IOException
	{
		RecMessage amsg= new RecMessage("FUPDATE");
		//set message with current helper list
		amsg.setHList(filterGroup(m));
		amsg.setMList(ChatMessageList.get(m.getFrom()));//sending the chat list
		//remove stored chat messages
		
		respondMsg(s,amsg);
		ChatMessageList.get(m.getFrom()).clear();
	}
	
	//filter the helpers according to the group the client is in
	public ConcurrentHashMap<String, HelperInfo> filterGroup(SendMessage m)
	{
		ConcurrentHashMap<String, HelperInfo> glist= new ConcurrentHashMap<String, HelperInfo>();
		String t= m.getGroupname();
		for(String key : helperList.keySet())
		{	//if the helper in the helperlist is in the group that the client is in, then add that helper to the temporary helperlist
			if(helperList.get(key).getGroupname().contains(t))
			{
				glist.put(key, helperList.get(key));
			}
		}
		return glist;
	}
	
	//the update from the helpers
	public void handleHelperUpdate(Socket s, SendMessage m) throws OptionalDataException, ClassNotFoundException, IOException
	{	
		//update geolocation
		HelperInfo tinfo=new HelperInfo(m.getFrom(), m.getLat(), m.getLong(), m.getGroupList(), m.getGroupMsg());
		if(!helperList.containsKey(m.getFrom()))
		{
			helperList.put(m.getFrom(), tinfo);
			System.out.println("New Helper added. User: "+ m.getFrom()+" lat " + m.getLat()+ " long "+m.getLong());
		
		}
		else 
		{
			helperList.replace(m.getFrom(), tinfo);
			System.out.println("Updating Helper. User: "+ m.getFrom());
		}
		
		
		//response
		RecMessage amsg= new RecMessage("HUPDATE");
		
		//set message with current chat list
		
		amsg.setMList(ChatMessageList.get(m.getFrom()));
		//remove stored chat messages		
		respondMsg(s,amsg);
		ChatMessageList.get(m.getFrom()).clear();
	}
	
	//updating the chatMessageList
	public void handleChatMessage(Socket s, SendMessage m)
	{System.out.println("Chat message sent from: "+ m.getFrom()+" To: " + m.getTo()+ " Message:  "+m.getMessage());
		ChatMessage msg= new ChatMessage(m.getFrom(), m.getMessage());
		ChatMessageList.get(m.getTo()).add(msg);
	}
	//response message sent from server to client
	private void respondMsg(Socket s, RecMessage amsg) throws OptionalDataException, ClassNotFoundException, IOException
	{
		ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(s.getOutputStream());		
				out.writeObject(amsg);
				System.out.println("Message responded");
				
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		s.close();
	}
	
//main used for starting the server 
	public static void main(String[] args)
	{
		RavenFinderServer serv = new RavenFinderServer();
		Thread serverThread = new Thread(serv);
		serverThread.start();
		try {
			serverThread.join(); // wait for server to be done, IF EVER !
		} catch (InterruptedException e) {
			System.out.println("Server shutdown");
			e.printStackTrace();
		} 
		
	}
}
