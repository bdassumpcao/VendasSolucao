package com.consultoriasolucao.appsolucaosistemas;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

public abstract class MyLocationManager {
	
	static Context context;
	
	private static final String TAG = "MyLocationManager";

	private Context mContext = null;
	
	private LocationManager mLocationManager = null;

	public MyLocationManager(Context context) {
		this.mContext = context;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);;
	}

	private GPSLocationListener gpsLocationListener = new GPSLocationListener();
	private NetworkLocationListener networkLocationListener = new NetworkLocationListener();
	
	public abstract void positionReceived(Location location);

	private class GPSLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			Log.d(TAG, "position received: " + location);
			if (location.getLatitude() == 0.0 && location.getLongitude() == 0.0 && location.getAccuracy() == 0.0 && location.getTime() == 0.0) {
				return;
			}
			positionReceived(location);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private class NetworkLocationListener implements LocationListener {

		public NetworkLocationListener() {
		}

		public void onLocationChanged(Location location) {
			Log.d(TAG, "position received: " + location);
			if (location.getLatitude() == 0.0 && location.getLongitude() == 0.0 && location.getAccuracy() == 0.0 && location.getTime() == 0.0) {
				return;
			}
			positionReceived(location);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	public void startLocationReceiving() {
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, gpsLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0F, networkLocationListener);
	}

	public void stopLocationReceiving() {
		mLocationManager.removeUpdates(gpsLocationListener);
		mLocationManager.removeUpdates(networkLocationListener);
	}
	
	public static boolean Conectado() {
		
        try {
            ConnectivityManager cm = (ConnectivityManager)
            context.getSystemService(Context.CONNECTIVITY_SERVICE);
            String LogSync = null;
            String LogToUserTitle = null;
            if (cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
                    LogSync += "\nConectado a Internet 3G ";
                    LogToUserTitle += "Conectado a Internet 3G ";
                    Log.d(TAG,"Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());
                    return true;
            } else if(cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
                    LogSync += "\nConectado a Internet WIFI ";
                    LogToUserTitle += "Conectado a Internet WIFI ";
                    //handler.sendEmptyMessage(0);
                    Log.d(TAG,"Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
                    return true;
            } else {
                    LogSync += "\nNão possui conexão com a internet ";
                    LogToUserTitle += "Não possui conexão com a internet ";
                    //handler.sendEmptyMessage(0);
                    Log.e(TAG,"Status de conexão Wifi: "+cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected());
                    Log.e(TAG,"Status de conexão 3G: "+cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected());
                    return false;
            }
        } catch (Exception e) {
                Log.e(TAG,e.getMessage());
                return false;
        }
    }
	
}