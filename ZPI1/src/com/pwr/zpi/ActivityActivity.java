package com.pwr.zpi;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ActivityActivity extends FragmentActivity implements OnClickListener{

	private GoogleMap mMap;
	private Button stopButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);
        
        stopButton = (Button) findViewById(R.id.stopButton);
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        
        stopButton.setOnClickListener(this);
        
        //skopiowane sk¹dœtam bo zacz¹³em gpsa, ale nic jeszcze nie zrobi³em
//     		Acquire a reference to the system Location Manager
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        Criteria crit = new Criteria();
//        crit.setAccuracy(Criteria.ACCURACY_FINE);
//        String provider = locationManager.getBestProvider(crit, true);
//        
//        Toast.makeText(this, locationManager.getLastKnownLocation(provider).toString(), Toast.LENGTH_LONG).show();
//        // Define a listener that responds to location updates
//        
//        mMap.setMyLocationEnabled(true);
//        LocationListener locationListener = new LocationListener() {
//            public void onLocationChanged(Location location) {
//              // Called when a new location is found by the network location provider.
//
//            }
//
//            public void onStatusChanged(String provider, int status, Bundle extras) {}
//
//            public void onProviderEnabled(String provider) {}
//
//            public void onProviderDisabled(String provider) {}
//          };
//
//        // Register the listener with the Location Manager to receive location updates
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//        
    }
    
	@Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.in_up_anim, R.anim.out_up_anim);
	}

	@Override
	public void onClick(View v) {
		if (v == stopButton)
		{
			//TODO finish and save activity
			finish();
			overridePendingTransition(R.anim.in_up_anim, R.anim.out_up_anim);
		}
		
	}
    
}
