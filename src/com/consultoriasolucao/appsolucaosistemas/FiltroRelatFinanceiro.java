package com.consultoriasolucao.appsolucaosistemas;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;


public class FiltroRelatFinanceiro extends Activity {

	
	private Button btdtini;
	private Button btdtfim;
	private int ano, mes, dia;
	boolean flagdataini;
	private DatabaseHelper db;
	private List<String> nomes = new ArrayList<String>();
	private Spinner categoria;
	private Spinner situacao;
	private Spinner ds_tipo;
	private String filtro;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filtrorelatfinanceiro);
		
		Calendar calendar = Calendar.getInstance();
		ano = calendar.get(Calendar.YEAR);
		mes = calendar.get(Calendar.MONTH);
		dia = calendar.get(Calendar.DAY_OF_MONTH);
		btdtini = (Button) findViewById(R.id.btdataini);
		btdtini.setText("01/" + (mes+1) + "/" + ano);
		
		btdtfim = (Button) findViewById(R.id.btdatafim);
		btdtfim.setText(dia + "/" + (mes+1) + "/" + ano);
		
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.ds_stiuacaotitulo,android.R.layout.simple_spinner_item);
		situacao = (Spinner) findViewById(R.id.ds_situacaosel);
		situacao.setAdapter(adapter);
		
		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.ds_tiporeceitadesp,android.R.layout.simple_spinner_item);
		ds_tipo = (Spinner) findViewById(R.id.ds_tiporeceitadespesa);
		ds_tipo.setAdapter(adapter1);
		
		db = new DatabaseHelper(this);
		carregarspinner();
		
	}
	
	public void carregarspinner()
	{
		SQLiteDatabase dbexe = db.getReadableDatabase();
		Cursor cursor = dbexe.rawQuery("SELECT _id, ds_categoria FROM categoria order by _id",	null);
		nomes.add("TODOS");
		while (cursor.moveToNext()) 
		{
		  nomes.add(cursor.getString(0)+"-"+cursor.getString(1));
		}
		
	    cursor.close();
		categoria = (Spinner) findViewById(R.id.categoriasel);
		
		//Cria um ArrayAdapter usando um padrão de layout da classe R do android, passando o ArrayList nomes
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, nomes);
		ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		categoria.setAdapter(spinnerArrayAdapter);
	}
	
	public void selecionarDataini(View view){
		showDialog(view.getId());
		 flagdataini = true;
		}
	public void selecionarDatafim(View view){
		showDialog(view.getId());
		flagdataini = false;
		}
	
	private OnDateSetListener listener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view,
		int year, int monthOfYear, int dayOfMonth) {
		ano = year;
		mes = monthOfYear;
		dia = dayOfMonth;
		
		if (flagdataini) //para não ter que criar outro método 
		{
		btdtini.setText(dia + "/" + (mes+1) + "/" + ano);
		} else		btdtfim.setText(dia + "/" + (mes+1) + "/" + ano);
		}
		};
		
	@Override
	protected Dialog onCreateDialog(int id) {
	if(R.id.btdataini == id){
	return new DatePickerDialog(this, listener, ano, mes, dia);
	}
	if(R.id.btdatafim == id){
		return new DatePickerDialog(this, listener, ano, mes, dia);
		}
	return null;
	}
	

	public void buscarRelat(View view)
	{
		filtro =btdtini.getText().toString()+"|"+btdtfim.getText().toString()+"|"+categoria.getSelectedItem().toString()+"|"+situacao.getSelectedItem().toString()+"|"+ds_tipo.getSelectedItem().toString()+"|";
		
		Intent intent = new Intent(this, RelatorioFinanceiro.class);
		intent.putExtra(RelatorioFinanceiro.EXTRA_NOME_USUARIO, filtro);
		startActivity(intent);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}
