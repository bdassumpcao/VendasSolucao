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
import android.widget.EditText;
import android.widget.Spinner;


public class FiltroGerenciarPedido extends Activity {

	
	private Button btdtini;
	private Button btdtfim;
	private EditText edtcd_cli;
	private int ano, mes, dia;
	boolean flagdataini;
	private DatabaseHelper db;
	private Button btcd_cli;
	private String filtro;
	public static final String EXTRA_CD_CLI = "com.consultoriasolucao.appsolucaosistemas.EXTRA_CD_CLI";
	private DatabaseHelper helper;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_filtrogerenciarpedido);
		
		Calendar calendar = Calendar.getInstance();
		ano = calendar.get(Calendar.YEAR);
		mes = calendar.get(Calendar.MONTH);
		dia = calendar.get(Calendar.DAY_OF_MONTH);
		btdtini = (Button) findViewById(R.id.btdataini);
		btdtini.setText(dia + "/" + (mes+1) + "/" + ano);
		
		btdtfim = (Button) findViewById(R.id.btdatafim);
		btdtfim.setText(dia + "/" + (mes+1) + "/" + ano);
		
		edtcd_cli = (EditText)findViewById(R.id.edtcd_cli); 
		btcd_cli = (Button) findViewById(R.id.btcd_cli);
		db = new DatabaseHelper(this);
		helper = new DatabaseHelper(this);
		
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
		if (edtcd_cli.getText().toString().equals(""))
		{
			edtcd_cli.setText("0");
		}
		filtro =btdtini.getText().toString()+"|"+btdtfim.getText().toString()+"|"+edtcd_cli.getText().toString()+"|";
		
		Intent intent = new Intent(this, GerenciarPedido.class);
		intent.putExtra(RelatorioFinanceiro.EXTRA_NOME_USUARIO, filtro);
		startActivity(intent);
	}
	
	
	public void consultaCliente(View view) {
		Intent intent = new Intent(this, ConsultaCliente.class);
		intent.putExtra(EXTRA_CD_CLI, true);
		startActivityForResult(intent, 1);
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK)
		{		
					
			edtcd_cli.setText(data.getStringExtra(EXTRA_CD_CLI));
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT  cd_cli, nm_cli from cliente where cd_cli="+  edtcd_cli.getText().toString(), null);
			cursor.moveToFirst();
			btcd_cli.setText(cursor.getString(1).toString());
		}
		
	}

	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}
