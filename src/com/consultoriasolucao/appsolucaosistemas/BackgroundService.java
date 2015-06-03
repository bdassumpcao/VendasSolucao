package com.consultoriasolucao.appsolucaosistemas;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.audiofx.BassBoost.Settings;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.widget.Button;

import com.github.kevinsawicki.http.HttpRequest;

public class BackgroundService extends Service {

	private ScheduledThreadPoolExecutor pool;
	private DatabaseHelper helper;
	private Integer ano,mes,dia;
	private String hora, minuto,segundo;
	private Date dt_lancamento;
	private Time horalancamento;
	private String usuario;
	private String vl_latitude;
	private String vl_longitude;
	private LocationManager lManager;
	private LocationListener lListener;
	Location localizacao;
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		helper = new DatabaseHelper(this);
		//pool = new ScheduledThreadPoolExecutor(1);
		// pool.scheduleAtFixedRate(new Worker(), 0, 5000,
		// TimeUnit.MILLISECONDS);
		SQLiteDatabase db1 = helper.getReadableDatabase();
		Cursor cursor = db1.rawQuery("SELECT _id, ds_usuario FROM licenca",	null);
		if (cursor.getCount()==0)
		{
		 usuario="Nao identificado";
		}else
		{
		  cursor.moveToFirst();
	      usuario=cursor.getString(1).toString();		
		 
		  if (! usuario.equals(""))
		  {
		    startGPS(); //so manda  localização se tiver usuário cadastrado
		  }
		} 
		 cursor.close();
		 db1.close();
		//pool.scheduleAtFixedRate(new WorkerGps(), 0, 60000,				TimeUnit.MILLISECONDS);  //15 minutos
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void gravaLocalizacao()
	{
		
		

		Calendar calendar = Calendar.getInstance();
		ano = calendar.get(Calendar.YEAR);
		mes = calendar.get(Calendar.MONTH);
		dia = calendar.get(Calendar.DAY_OF_MONTH);
		
		hora =calendar.get(Calendar.HOUR_OF_DAY)+"";
		minuto =calendar.get(Calendar.MINUTE)+"";
		segundo =calendar.get(Calendar.SECOND)+"";
		
		//formatando a hora
	    
		if (hora.length()==1) 
		{
			hora="0"+hora;
		}
		
		if (minuto.length()==1) 
		{
			minuto="0"+minuto;
		}
		if (segundo.length()==1) 
		{
			segundo="0"+segundo;
		}
		
		String data = dia + "/" + (mes + 1) + "/" + ano;
		dt_lancamento	 = ConvertToDate(data);
				
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dt_lancamento", dt_lancamento.getTime());
		values.put("hr_lancamento",  hora+":"+minuto+":"+segundo );
		
		
		values.put("vl_latitudec",vl_latitude);
		values.put("vl_longitudec",vl_longitude);
		values.put("ds_usuario", usuario);

		db.insert("coordenadagps", null, values);
		db.close();
		
		new AsyncTask (){

			@Override
			protected Object doInBackground(Object... arg0) {
				int code = HttpRequest.get("http://www.consultoriasolucao.com/testes.php?nm_usuario="+usuario+"&vl_latitude="+vl_latitude+"&vl_longitude="+vl_longitude).code();
				if(code == 200){
					//resposta ok
					//Toast.makeText(GerenciarLocalizacao.this, "OK", Toast.LENGTH_LONG).show();
				}else{
					//falha
				///	Toast.makeText(GerenciarLocalizacao.this, "falha", Toast.LENGTH_LONG).show();
				}
				return null;
			}
			
			
		
		}.execute();
		
		
	}
	
	
	

	
  
    public void desabilitargps()
    {
    	// lManager.removeUpdates(lListener);	
    }
    
	private Date ConvertToDate(String dateString)
	{
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    Date convertedDate = new Date();
	    try {
	        convertedDate = dateFormat.parse(dateString);
	    } catch (ParseException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return convertedDate;
	}
	
	
	// Método que faz a leitura de fato dos valores recebidos do GPS
		public void startGPS() {
			lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			lListener = new LocationListener() {
				public void onLocationChanged(Location locat) {

					vl_latitude = locat.getLatitude()+"";
					vl_longitude = locat.getLongitude()+"";
					gravaLocalizacao();


					desabilitargps();
				}

				public void onStatusChanged(String provider, int status,
						Bundle extras) {

				}

				public void onProviderEnabled(String provider) {
				}

				public void onProviderDisabled(String provider) {
				}
			};// fim location listenar
  
			lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 0,
					lListener);
			
		

		}

		
	
	private Location getBestKnownLocation() {
		//startGPS();		 
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	
		Location bestLocation = null;
		List<String> providers = Arrays.asList(
				LocationManager.GPS_PROVIDER,
				LocationManager.NETWORK_PROVIDER,
				LocationManager.PASSIVE_PROVIDER);
		for (String provider : providers) {
			Location location = locationManager.getLastKnownLocation(provider);
			if (location != null) {
				bestLocation = location;
				return location;
			}
		}

		
		return bestLocation;
		
	}

	private class Worker implements Runnable {

		@Override
		public void run() {
			// TODO buscar no banco, e criar a notificacao
			Log.i(getPackageName(), "rodou....");
			criarNotificacao("", "Teste", 0);
		}

	}

	private void criarNotificacao(String usuario, String texto, int id) {
		int icone = R.drawable.ic_launcher;
		String aviso = "PENDENCIA";
		long data = System.currentTimeMillis();
		String titulo = usuario + " ";
		Context context = getApplicationContext();
		Intent intent = new Intent(context, SolucaoSistemas.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(context, id,
				intent, Intent.FLAG_ACTIVITY_NEW_TASK);

		Notification notification = new Notification(icone, aviso, data);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.defaults |= Notification.DEFAULT_SOUND;
		notification.setLatestEventInfo(context, titulo, texto, pendingIntent);
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager notificationManager = (NotificationManager) getSystemService(ns);
		notificationManager.notify(id, notification);
	}

}
