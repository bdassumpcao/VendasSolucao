package com.consultoriasolucao.appsolucaosistemas;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Categoria extends Activity {
	private DatabaseHelper helper;
	
	EditText edtds_categoria;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categoria);
		this.edtds_categoria = (EditText)findViewById(R.id.edtds_categoria);
		// prepara acesso ao banco de dados
		helper = new DatabaseHelper(this);	
		
		
		
	}
	
	public void inserirCategoria(View view)
	{
		Boolean flagvalida = true;

		if (edtds_categoria.getText().toString().equals("")) {
			Toast.makeText(this, "Entre com a descrição",
					Toast.LENGTH_LONG).show();
			flagvalida = false;

		}
		
		if (flagvalida)
		{
			
		
		SQLiteDatabase db = helper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put("ds_categoria", this.edtds_categoria.getText().toString());
		long resultado = db.insert("categoria", null, values);
	
		
		this.edtds_categoria.setText("");
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}
