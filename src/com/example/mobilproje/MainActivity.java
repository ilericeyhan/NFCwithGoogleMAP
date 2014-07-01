package com.example.mobilproje;


import java.math.BigInteger;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
 

public class MainActivity extends Activity {
	
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String TAG = "NfcDemo";
    private NfcAdapter mAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate.");
        setContentView(R.layout.mainac);
        
        mAdapter = NfcAdapter.getDefaultAdapter(this);  
        
     
        if (!mAdapter.isEnabled()) {
        	Toast.makeText(this, "Why did you disabled NFC?", Toast.LENGTH_LONG).show();
        }
         
        handleIntent(getIntent());
    }
   
    @Override
    protected void onResume() {
    	setupForegroundDispatch(this, mAdapter);
    	super.onResume();
    } 
    
    @Override
    protected void onPause() {
    	stopForegroundDispatch(this, mAdapter);        
        super.onPause();
    }   
    
    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        System.out.println("Setup FGD.");
 
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
 
        IntentFilter[] filters = new IntentFilter[1];
        String[][] techList = new String[][]{};
 
        // Notice that this is the same filter as in our manifest.
        filters[0] = new IntentFilter();
        //filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addAction(NfcAdapter.ACTION_TAG_DISCOVERED); 
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType("*/*");   //(MIME_TEXT_PLAIN);
        } catch (MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
         
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
        System.out.println("Enabled FGD.");
    }
   
    protected void onNewIntent(Intent intent) { 
    	System.out.println("Intent.");
//    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        handleIntent(intent);
    }
      
    private void handleIntent(Intent intent) {   	
    	System.out.println("Handle.");
        String action = intent.getAction();
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
        	System.out.println("NDEF discovered.");
             
            String type = intent.getType();
            if (MIME_TEXT_PLAIN.equals(type)) {
     
                //Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                 
            } else {
                Log.d(TAG, "Wrong mime type: " + type);
            }
        } else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
        	System.out.println("TECH discovered.");

		            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		            byte[] ID = tag.getId();	 
		            Intent i1 = new Intent(MainActivity.this, Servis.class);
		            i1.putExtra("UserID", bin2hex(ID));
		        	startService(i1);	
		        	System.out.println("Ana Aktiviteden Servise Gönderilen ID : "+ bin2hex(ID));
                }
            }

    static String bin2hex(byte[] data) {
        return String.format("%0" + (data.length * 2) + "X", new BigInteger(1,data));
    }
  }
