package com.example.mobilproje;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver{

		static String id;
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println("onReceive fonksiyonu.");
			id 			= intent.getStringExtra(Servis.USERID);
			System.out.println("Receiver ID : " + id);
		}
	}