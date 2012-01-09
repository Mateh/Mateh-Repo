/* Matthew Chou 100703266 Honours Project - RavenFinder. 2011. Professor: Tony White.
 * The MessagingPage class (Activity) is the activity in which the user can see the messages that they are being sent from other users. It uses a list to show the current user chat log that they
 * are focused on. The focus/user shown is changed by using the onFling() gesture which allows the user to switch between chatters with the flick of a finger.
 * 
 */
package Mateh.Android;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import Mateh.Android.MessagingPage_Helper.timerUpdate;
import MessageData.SendMessage;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is the main Activity that displays GUI.
 */
public class MessagingPage extends Activity  implements OnClickListener, OnGestureListener{

    
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
  
    private TextView tview;
    
    //timer used to update the view of chat messages from the list of chat messages getting updated in Finder_Main
    private Timer chatUpdateTimer;
    private Handler handler;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.messagescreenpanellayout);
        gestureDetector = new GestureDetector(this);
        View messangerView = (View) findViewById(R.id.in);
        //setting the on touch listener
        messangerView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (gestureDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });
       
        tview= (TextView) findViewById(R.id.msgpanel_text);
        chatLineText= (EditText) findViewById(R.id.edit_text_out);
        
        handler=new Handler();
        chatUpdateTimer=new Timer();
        chatUpdateTimer.scheduleAtFixedRate(new timerUpdate(), 0,2000);
        //restart the finder update timer since it was cancelled when the activity is paused
        Finder_Main.myFinder_main.scheduleTimer(Finder_Main.myFinder_main.getUpdateSeconds());
    }
    
    //the TimerTask that updates the chat panel with messages
    //attempted to use a handler in this case becuase the updateChatPanel method is modifiying the GUI,and if your no in the main GUI thread, which a timer is a sepearate thread, you can not modify it without breaking
   
    class timerUpdate extends TimerTask {
        public void run() {
        	
        	handler.post(new Runnable() {
				public void run() {
				
					updateChatPanel();
				}
			});
          
        }
    }
    //this method updates the currently being viewed chat message log with another user
    public synchronized void updateChatPanel()
    {
    	//clear chat panel
    	mConversationArrayAdapter.clear(); 	 
    	//returns the name of the current chatter the client is focused on
    	String chatN= Finder_Main.chatterIdMap.get(Finder_Main.currentChatter);
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

    
    //sets the lists of chat messages
    private void setupChat() {
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
        conversationList = (ListView) findViewById(R.id.in);
        conversationList.setAdapter(mConversationArrayAdapter);

        // Initialize the compose field with a listener for the return key
        chatLineText.setOnEditorActionListener(mWriteListener);

        // Initialize the send button with a listener that for click events
        mSendButton = (Button) findViewById(R.id.chat_button_send);
        mSendButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	//if(D) Log.e(TAG, "[sendButton clicked]");
                // Send a message using content of the edit text widget
                EditText view = (EditText) findViewById(R.id.edit_text_out);
               
                
                String message = view.getText().toString();
                sendMessage(message);
                
            }
        });      
    }
   
    public synchronized void onResume() {
        super.onResume();
        Finder_Main.myFinder_main.onResume();
        Finder_Main.myFinder_main.locCheck=1;
        
    }

    
    public synchronized void onPause() {
        super.onPause(); 
       Finder_Main.myFinder_main.locCheck=0;
        Finder_Main.myFinder_main.onPause();
    }
    public void onStop() {
        super.onStop();
    }
    public void onDestroy() {
        super.onDestroy();
    }
    
    //method that is called when the enter key or submit button pressed to send a chat
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
        String chatN= Finder_Main.chatterIdMap.get(Finder_Main.currentChatter);
        SendMessage msg=new SendMessage("MESSAGE", MenuActivity.username, MenuActivity.groupName);
        msg.setMessage(message);
        msg.setTo(chatN);
        
        FinderMessenger mess=new FinderMessenger("MESSAGE", Finder_Main.myFinder_main,msg);
		Thread t= new Thread(mess);
		t.start();
        MenuActivity.chatLog.get(chatN).add("Me:  " + message);
        updateChatPanel();
    }


    // The action listener for the EditText widget, to listen for the return key
    private TextView.OnEditorActionListener mWriteListener =
        new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL 				// return key 
            		&& event.getAction() == KeyEvent.ACTION_UP) // the key has been released
            {
                String message = view.getText().toString();
                sendMessage(message);
            }
           
            return true;
        }
    };


 
    //not used
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
      
      if(xvelocity > MessagingPage.SWIPE_MIN_VELOCITY && xdiff > MessagingPage.SWIPE_MIN_DISTANCE)
      {
              if(ev1x > ev2x) //Swipe Left
              {
            	//   Toast.makeText(this, "To the Left", Toast.LENGTH_SHORT).show();
            	  if(Finder_Main.currentChatter!=0)
            	  {
            		  Finder_Main.currentChatter--;
            	  }
            	  updateChatPanel();
              }
              else //Swipe Right
              {
            	//   Toast.makeText(this, "To the right", Toast.LENGTH_SHORT).show();  
            	  if(Finder_Main.currentChatter!=Finder_Main.numberOfChatters)
            	  {
            		  Finder_Main.currentChatter++;
            	  }
            	  updateChatPanel();
              }
              
      }
        return false;
 
	}
    
    //unused inherited methods
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