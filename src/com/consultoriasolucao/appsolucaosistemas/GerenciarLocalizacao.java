package com.consultoriasolucao.appsolucaosistemas;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.github.kevinsawicki.http.HttpRequest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GerenciarLocalizacao extends Activity {

	TextView txtvl_longitude;
	TextView txtvl_latitude;
	private Date dt_aux;
	private DatabaseHelper helper;
	private ListView lista;
	private List<Map<String, String>> produtos;
	private String usuario;
	private Integer ano, mes, dia;
	private String hora, minuto, segundo;
	private Date dt_lancamento;
	private LocationManager lManager;
	private LocationListener lListener;
	private TextView txtcoordenadas;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gerenciarlocalizacao);
		this.txtvl_latitude = (TextView) findViewById(R.id.txtvl_latitude);
		this.txtvl_longitude = (TextView) findViewById(R.id.txtvl_longitude);
		this.lista = (ListView) findViewById(R.id.listacoordenadas);
		this.txtcoordenadas = (TextView)findViewById(R.id.txtcoordenadas);
		// prepara acesso ao banco de dados
		helper = new DatabaseHelper(this);
		

	}
	
	public void escrevePhp()
	{
		
 //	 Uri uri = Uri.parse("http://www.consultoriasolucao.com/testes.php?nm_usuario="+usuario+"&vl_latitude="+txtvl_latitude.getText()+"&vl_longitude="+txtvl_longitude.getText());
//		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	//	startActivity(intent);
		new AsyncTask (){

			@Override
			protected Object doInBackground(Object... arg0) {
				int code = HttpRequest.get("http://www.consultoriasolucao.com/testes.php?nm_usuario=testes&vl_latitude=00&vl_longitude=22").code();
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
 
	// Método que faz a leitura de fato dos valores recebidos do GPS
	public void startGPS() {
		lManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		lListener = new LocationListener() {
			public void onLocationChanged(Location locat) {

				txtvl_latitude.setText(locat.getLatitude() + "");
				txtvl_longitude.setText(locat.getLongitude() + "");
				txtcoordenadas.setText(txtvl_latitude.getText()+","+txtvl_longitude.getText());
				gravaLocalizacao();
				atualizagrid();

				desabilitargps();
			//	escrevePhp();
			
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}

			public void onProviderEnabled(String provider) {
			}

			public void onProviderDisabled(String provider) {
			}
		};// fim location listenar

		lManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,
				lListener);

	}

	public void desabilitargps() {
		lManager.removeUpdates(lListener);
	}

	public void veririficalocalizacao(View v) {

		//startGPS();
		atualizagrid();
		
		
		
		

	}

	private Date ConvertToDate(String dateString) {
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

	public void apagalocalizacao(View view) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from coordenadagps");
		atualizagrid();

	}

	private List<Map<String, String>> buscarCoordenadas() {
		// buscar todos os produtos do banco

		Cursor c = helper
				.getReadableDatabase()
				.rawQuery(
						"select _id, vl_latitudec, vl_longitudec,dt_lancamento,hr_lancamento,ds_usuario from coordenadagps order by _id desc ",
						null);
		produtos = new ArrayList<Map<String, String>>();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		while (c.moveToNext()) {
			Map<String, String> mapa = new HashMap<String, String>();
			dt_aux = new Date(c.getLong(3));
			mapa.put("dt_lancamentocoordenada", dateFormat.format(dt_aux));
			mapa.put("hr_lancamentocoordenada", c.getString(4));
			mapa.put("vl_latitudec", c.getString(1));
			mapa.put("vl_longitudec", c.getString(2));
			mapa.put("ds_usuario", c.getString(5));
			produtos.add(mapa);
		}
		c.close();
		helper.close();
		// construir objeto de retorno que é uma String[]
		return produtos;
	}

	public void atualizagrid() {
		String de[] = { "dt_lancamentocoordenada", "hr_lancamentocoordenada",
				"vl_latitudec", "vl_longitudec", "ds_usuario" };
		int para[] = { R.id.dt_lancamentocoordenada,
				R.id.hr_lancamentocoordenada, R.id.vl_latitudec,
				R.id.vl_longitudec, R.id.ds_usuario };

		SimpleAdapter adapter = new SimpleAdapter(this, buscarCoordenadas(),
				R.layout.listview_coordenadas, de, para);

		lista.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

	public void gravaLocalizacao() {
		SQLiteDatabase db1 = helper.getReadableDatabase();
		Cursor cursor = db1.rawQuery("SELECT _id, ds_usuario FROM licenca",
				null);
		if (cursor.getCount() == 0) {
			usuario = "Nao identificado";
		} else {
			cursor.moveToFirst();
			usuario = cursor.getString(1);
		}

		cursor.close();

		Calendar calendar = Calendar.getInstance();
		ano = calendar.get(Calendar.YEAR);
		mes = calendar.get(Calendar.MONTH);
		dia = calendar.get(Calendar.DAY_OF_MONTH);

		hora = calendar.get(Calendar.HOUR_OF_DAY) + "";
		minuto = calendar.get(Calendar.MINUTE) + "";
		segundo = calendar.get(Calendar.SECOND) + "";

		// formatando a hora

		if (hora.length() == 1) {
			hora = "0" + hora;
		}

		if (minuto.length() == 1) {
			minuto = "0" + minuto;
		}
		if (segundo.length() == 1) {
			segundo = "0" + segundo;
		}

		String data = dia + "/" + (mes + 1) + "/" + ano;
		dt_lancamento = ConvertToDate(data);

		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("dt_lancamento", dt_lancamento.getTime());
		values.put("hr_lancamento", hora + ":" + minuto + ":" + segundo);

		values.put("vl_latitudec",
				Double.valueOf(String.valueOf(txtvl_latitude.getText()))
						.doubleValue());
		values.put("vl_longitudec",
				Double.valueOf(String.valueOf(txtvl_longitude.getText()))
						.doubleValue());
		values.put("ds_usuario", usuario);

		db.insert("coordenadagps", null, values);
		db.close();
		atualizagrid();

	}

}
