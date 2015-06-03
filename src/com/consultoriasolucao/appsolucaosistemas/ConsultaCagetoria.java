package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.SimpleAdapter.ViewBinder;

public class ConsultaCagetoria extends Activity implements
OnItemClickListener{

	private ListView lista;

	private DatabaseHelper db;
	private List<Map<String, String>> produtos;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consultacategoria);
	    lista = (ListView) findViewById(R.id.listacagetoria);

		db = new DatabaseHelper(this);

		
		
		

		
		lista.setOnItemClickListener(this);
		registerForContextMenu(lista); 
	
	}

		
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
	ContextMenuInfo menuInfo) {
	MenuInflater inflater = getMenuInflater();
	inflater.inflate(R.menu.menu_categoria, menu);
	}

	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		
	    switch (item.getItemId()) {
	        case R.id.remover_categoria:
	        	//Toast.makeText(this, item.toString(), Toast.LENGTH_LONG).show();
	        	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
	        	
	        	String cd_categoria = produtos.get(info.position).get("cd_categoria");
	        	produtos.remove(info.position);
	        	lista.invalidateViews();
	        	
	        	 db.getWritableDatabase().execSQL("delete from categoria where _id ="+cd_categoria);
	        	 
	            return true;
	        
	        default:
	            return super.onContextItemSelected(item);
	    }
	}
     
     
	private List<Map<String, String>> buscarCategoria() {
		// buscar todos os produtos do banco

		Cursor c = db
				.getReadableDatabase()
				.rawQuery(
						"select _id, ds_categoria from categoria order by ds_categoria",
						null);
		produtos = new ArrayList<Map<String, String>>();
		
		while (c.moveToNext()) {
			Map<String, String> mapa = new HashMap<String, String>();

			mapa.put("cd_categoria",  c.getString(0));
			mapa.put("ds_categoria",c.getString(1));
			
			produtos.add(mapa);
		}
		
		c.close();
		
				
		
		return produtos;
	}

	public void buscarCategoria(View view) {
		String de[] = { "cd_categoria", "ds_categoria"};
		int para[] = { R.id.cd_categoria, R.id.ds_categoria	};

		SimpleAdapter adapter = new SimpleAdapter(this, buscarCategoria(),
				R.layout.listview_categoria, de, para);

		lista.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;

	}

	@Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
          
    }

}
