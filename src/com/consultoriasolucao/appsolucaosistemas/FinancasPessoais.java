package com.consultoriasolucao.appsolucaosistemas;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;


public class FinancasPessoais extends Activity {

	private DatabaseHelper helper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_financaspessoais);
		
		//procedimento para verificar se as categorias padrões foram lançadas
		helper = new DatabaseHelper(this);	
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT _id, ds_categoria FROM categoria",	null);
		
		if (cursor.getCount() == 0) //caso ainda nao tenha nehuma categoria cadastrada então cadastra 
		{
			SQLiteDatabase dbexec = helper.getWritableDatabase();
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Geral'); ");
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Educacao'); ");
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Saude'); ");
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Combustivel'); ");
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Lazer Viagens Entreterimento'); ");
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Investimentos Financiamentos'); ");
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Supermercado'); ");
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Vestuario'); ");
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Beleza e Estetica'); ");
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Aluguel'); ");
			dbexec.execSQL("INSERT INTO categoria (ds_categoria) values ('Fixos: Agua Luz Fone'); ");
			dbexec.close();
			
		}
		cursor.close();
		
		
		
		
	}
	
	public void listCategoria(View view)
	{
		startActivity(new Intent(this, ConsultaCagetoria.class));
	}

	public void lancaDespesa(View view)
	{
		startActivity(new Intent(this, LancaDespesa.class));
	}
	
	public void cadCategoria(View view)
	{
		startActivity(new Intent(this, Categoria.class));
	}
	
	
	public void relatorioFinanceiro(View view)
	{
		startActivity(new Intent(this,FiltroRelatFinanceiro.class));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}
