package com.example.mobilproje;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class Servis extends Service {
	
	private final IBinder myBinder = new MyLocalBinder();
	static final String MAP = "MyService";
	static final String USERID = "";
	static final String TIMESTAMP = "";
		
	Intent intent1 = new Intent(MAP);
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
	    System.out.println("Servis Baþlatýldý.");
		
	    String id = intent.getStringExtra("UserID");
		
		intent1.putExtra(USERID, id);
		
		System.out.println("Servisten Gönderilen ID : " 	  + String.valueOf(id));
		sendBroadcast(intent1);
		
    	Intent i2 = new Intent(Servis.this, MapFragment.class);
    	i2.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                     
    	i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	startActivity(i2);
		
	    return START_REDELIVER_INTENT;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
				return myBinder;
	}


	public class MyLocalBinder extends Binder {
        Servis getService() {
            return Servis.this;
        }
	}
	
	@Override
	public void onCreate() {
		Log.d(MAP, "onCreate");
	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
		Log.d(MAP, "onDestroy");
	}
	
	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "My Service Started", Toast.LENGTH_LONG).show();
		Log.d(MAP, "onStart");
	}
}

