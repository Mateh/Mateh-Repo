/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * The MessagingPage_Helper functions the same way as the MessagingPage class. MessagingPage_Helper is the activity in which the user can see the messages that they are being sent from other users. It uses a list to show the current user chat log that they
 * are focused on. The focus/user shown is changed by using the onFling() gesture which allows the user to switch between chatters with the flick of a finger.
 * 
 */
 package Mateh.Android;


import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import MessageData.SendMessage;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays GUI.
 */
public class MessagingPage_Helper extends Activity  implements OnClickListener, OnGestureListener{
 
    // Layout Views
    private ListView conversationList;
    private EditText chatLineText;
    private Button mSendButton;
    private GestureDetector gestureDetector;
    // Name of the connected device
    private ArrayAdapter<String> mConversationArrayAdapter;
    private StringBuffer mOutStringBuffer;
    
  //these constants are used for onFling
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MIN_VELOCITY = 200;
   
    
    //constants for chatlogs
    private int currentChatter=0;
    private int numberOfChatters=0;
    private HashMap<Integer, String> chatterIdMap;
    private TextView tview;
    private Timer chatUpdateTimer;
    private Handler handler;
    public static MessagingPage_Helper msghelper;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        msghelper=this;
        
        setContentView(R.layout.messagescreenpanellayout);
        gestureDetector = new GestureDetector(this);
        View messangerView = (View) findViewById(R.id.in);
        messangerView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });
        chatterIdMap = new HashMap<Integer,String>();
        tview= (TextView) findViewById(R.id.msgpanel_text);
        chatLineText= (EditText) findViewById(R.id.edit_text_out);
        handler=new Handler();
        chatUpdateTimer=new Timer();
        chatUpdateTimer.scheduleAtFixedRate(new timerUpdate(), 0,2000);
        
        
    }  
    //the TimerTask that updates the chat panel with messages
    //attempted to use a handler in this case becuase the updateChatPanel method is modifiying the GUI,and if your no in the main GUI thread, which a timer is a sepearate thread, you can not modify it without breaking
    
    
    //the TimerTask
    class timerUpdate extends TimerTask {
        public void run() {
        	handler.post(new Runnable() {
				public void run() {
				//	Log.e("TESTING", "updating");
					updateChatPanel();
				}
			});
          
        }
    }
    
    public synchronized void updateChatPanel()
    {
    	//clear chat panel
    	mConversationArrayAdapter.clear();
    	
    	int j=0;
    	numberOfChatters=(MenuActivity.chatLog.size()-1);
    	//loop through chatlog to find current focused chatter
    	for(String key: MenuActivity.chatLog.keySet())
		{
		chatterIdMap.put(j, key);
		j++;
		}
    	//returns the name of the current chatter the client is focused on
    	String chatN= chatterIdMap.get(currentChatter);
    	tview.setText(chatN);
    	if(MenuActivity.chatLog.get(chatN)!=null){
	    	for(String msgs : MenuActivity.chatLog.get(chatN))
	    	{
	    		mConversationArrayAdapter.add(msgs);
	    	}
    	}
    	
    }
  
    //on a onTouchEvent, relay that event to the gestureDector
    @Override
    public boolean onTouchEvent(MotionEvent event){
    	return gestureDetector.onTouchEvent(event);
    }
    //when the activity starts, setup the chat and update it
    public void onStart() {
        super.onStart();
        setupChat();
        updateChatPanel();
    }

    
    public synchronized void onResume() {
        super.onResume();
        
        Helper_Main.myHelper_Main.onResume();
        Helper_Main.myHelper_Main.locCheck=1;
    }

    //sets the lists of chat messages
    private void setupChat() {
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        conversationList = (ListView) findViewById(R.id.in);
        conversationList.setAdapter(mConversationArrayAdapter);


        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.chat_button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	
                // Send a message using content of the edit text widget
                TextView view = (TextView) findViewById(R.id.edit_text_out);
               
                
                String message = view.getText().toString();
                sendMessage(message);
                
            }
        });

        updateChatPanel();
    }
    
    
    public synchronized void onPause() {
        super.onPause();
        Helper_Main.myHelper_Main.locCheck=0;
        Helper_Main.myHelper_Main.onPause();
    }

   
    public void onStop() {
        super.onStop(); 
       
    }

    
    public void onDestroy() {
        super.onDestroy();
      
    }
    
    
    private void sendMessage(String message) {
    	
        // Check that there's actually something to send
        if (message.length() > 0 ) {
            //clear the edittext field
        	chatLineText.setText("");
        }else{
        	
            Toast.makeText(this, "Please type a message", Toast.LENGTH_SHORT).show();
            return;
            
        } 
        
        // send message to other user and add the message to appropriate chat log
        String chatN= chatterIdMap.get(currentChatter);
        SendMessage msg=new SendMessage("MESSAGE", MenuActivity.username, MenuActivity.groupName);
        msg.setMessage(message);
        msg.setTo(chatN);
        
        HelperMessenger mess=new HelperMessenger("MESSAGE", Helper_Main.myHelper_Main,msg);
		Thread t= new Thread(mess);
		t.start();
        MenuActivity.chatLog.get(chatN).add("Me:  " + message);
        updateChatPanel();
    }



 
    
    public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
 
    //the onFling method is the swiping of left or right on the screen 
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {
    	 
      
      final float ev1x = e1.getX();
      final float ev1y = e1.getY();
      final float ev2x = e2.getX();
      final float ev2y = e2.getY();
      final float xdiff = Math.abs(ev1x - ev2x);
     // final float ydiff = Math.abs(ev1y - ev2y);
      final float xvelocity = Math.abs(velocityX);
     // final float yvelocity = Math.abs(velocityY);
      
      if(xvelocity > MessagingPage_Helper.SWIPE_MIN_VELOCITY && xdiff > MessagingPage_Helper.SWIPE_MIN_DISTANCE)
      {
              if(ev1x > ev2x) //Swipe Left
              {
            	  // Toast.makeText(this, "To the Left", Toast.LENGTH_SHORT).show();
            	  if(currentChatter!=0)
            	  {
            		  currentChatter--;
            	  }
            	  updateChatPanel();
              }
              else //Swipe Right
              {
            	//   Toast.makeText(this, "To the right", Toast.LENGTH_SHORT).show();  
            	  if(currentChatter!=numberOfChatters)
            	  {
            		  currentChatter++;
            	  }
            	  updateChatPanel();
              }
              
      }
        return false;
 
	}
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
 
	}
 
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
 
	}
 
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}
 
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
    	return false;
    }


	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}
}