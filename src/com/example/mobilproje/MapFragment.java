package com.example.mobilproje;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.Menu;
import android.widget.Toast;



public class MapFragment extends FragmentActivity{	

	Time today = new Time(Time.getCurrentTimezone()); 
	String date;
	String time;
	double latitude;
	double longitude;
	
	String lat;
	String lng;
	String label;
	
	SharedPreferences sharedPreferences;    
	int locationCount = 0;
	String string;
	private GoogleMap googleMap;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.map_fragment);
	    
	    GPSTracker tracker = new GPSTracker(this);
	    if (tracker.canGetLocation() == false) {
	        tracker.showSettingsAlert();
	    } else {
	        latitude = tracker.getLatitude();
	        longitude = tracker.getLongitude();
	    }
	    
	    LatLng EvKoordinat = new LatLng(latitude,longitude); 
	    
	 // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if(status!=ConnectionResult.SUCCESS){ // Google Play Services are not available
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }else { // Google Play Services are available            
            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.haritafragment);
            // Getting GoogleMap object from the fragment
            googleMap = fm.getMap();
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
    	    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EvKoordinat, 15)); 
                        
            
            // Opening the sharedPreferences object
            sharedPreferences = getSharedPreferences("location", 0);            
            // Getting number of locations already stored
            locationCount = sharedPreferences.getInt("locationCount", 0);                        
            // Getting stored zoom level if exists else return 0
            String zoom = sharedPreferences.getString("zoom", "0"); 
            
        	today.setToNow();
        	date = today.monthDay + "." + (today.month+1) + "." + today.year;
        	time = today.format("%k:%M:%S");

        	String etiket = MyReceiver.id + " " + date + " " + time; 
               
            locationCount++;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("lat"+ Integer.toString((locationCount-1)), Double.toString(EvKoordinat.latitude));            
            editor.putString("lng"+ Integer.toString((locationCount-1)), Double.toString(EvKoordinat.longitude));
            editor.putInt("locationCount", locationCount);                               
            editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));
            editor.putString("title" + Integer.toString((locationCount)-1), etiket);  
            editor.commit();
            
//            googleMap.addMarker(new MarkerOptions().position(EvKoordinat).title("Tag ID : " + bcr.id + " --" + " Date : " + date + " --" + " Time : " + time));
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(EvKoordinat, 13)); 
            
            // If locations are already saved
            if(locationCount!=0){              
                
            	lat = "";
            	lng = "";
            	label = "";
                
                // Iterating through all the locations stored
                for(int i=0;i<locationCount;i++){                    
                    // Getting the latitude of the i-th location
                    lat = sharedPreferences.getString("lat"+i,"0");                   
                    // Getting the longitude of the i-th location
                    lng = sharedPreferences.getString("lng"+i,"0");  
                    label = sharedPreferences.getString("title"+i,"0"); 
                    System.out.println("For içi label : " + label);
                    // Drawing marker on the map
                    
                    drawMarker(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), label);                    
                }
                
                // Moving CameraPosition to last clicked position
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng))));                
                // Setting the zoom level in the map on last position  is clicked
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(Float.parseFloat(zoom)));                               
            }           
        }        
        
        
    }
	
     
    private void drawMarker(LatLng point, String title){
        // Creating an instance of MarkerOptions
    	System.out.println("DrawMarkerLabel : " + title);
        MarkerOptions markerOptions = new MarkerOptions();                                
        // Setting latitude and longitude for the marker
        markerOptions.position(point).title(title);
        System.out.println(markerOptions.position(point).title(title));
        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);            
    }
        
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	   
	
	@Override
	public void onResume() {
		super.onResume();
		googleMap.setOnMapClickListener(new OnMapClickListener() {           
            @Override
            public void onMapClick(LatLng point) {        
            	
            	today.setToNow();
            	date = today.monthDay + "." + (today.month+1) + "." + today.year;
            	time = today.format("%k:%M:%S");
            	
                locationCount++;                                   
                // Drawing marker on the map                
                /** Opening the editor object to write data to sharedPreferences */
                SharedPreferences.Editor editor = sharedPreferences.edit();                            
                // Storing the latitude for the i-th location
                editor.putString("lat"+ Integer.toString((locationCount-1)), Double.toString(point.latitude));                
                // Storing the longitude for the i-th location
                editor.putString("lng"+ Integer.toString((locationCount-1)), Double.toString(point.longitude));                
                // Storing the count of locations or marker count
                editor.putInt("locationCount", locationCount);                               
                /** Storing the zoom level to the shared preferences */
                editor.putString("zoom", Float.toString(googleMap.getCameraPosition().zoom));  
                editor.putString("title" + Integer.toString((locationCount)-1), Integer.toString(locationCount) + "----" + date + time); 
                /** Saving the values stored in the shared preferences */
                editor.commit();                            
                Toast.makeText(getBaseContext(), "Marker is added to the Map", Toast.LENGTH_SHORT).show();  
                drawMarker(point, Integer.toString(locationCount) + "\\n" + date + "\\n" + time );
            }
        });    
        
        
        googleMap.setOnMapLongClickListener(new OnMapLongClickListener() {                 
            @Override
            public void onMapLongClick(LatLng point) {               
                // Removing the marker and circle from the Google Map
                googleMap.clear();               
                // Opening the editor object to delete data from sharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();                
                // Clearing the editor
                editor.clear();               
                // Committing the changes
                editor.commit();                
                // Setting locationCount to zero
                locationCount=0;                
            }
        });                 
		
	} 	
	
	@Override
	public void onPause() {		
		super.onResume();
	} 	
}

