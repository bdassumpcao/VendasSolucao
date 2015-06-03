package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class DadosCliente extends Activity {

	TextView txtcd_cli;
	TextView txtnm_cli;
	TextView txtnr_tel;
	TextView txtnr_fax;
	TextView txtnr_cel;
	TextView txtnm_rua;
	TextView txtnm_rzascl;
	TextView txtnr_cpfcnpj;
	TextView txtnr_rgie;
	TextView txtnm_cpm;
	TextView txtnr_cep;
	TextView txtds_obs;
	TextView txtds_email;
	TextView txtnm_brr;
	TextView txtds_complemento;
	TextView txtcd_uf;
	public static final String ACAO_EXIBIR_SAUDACAO = "com.consultoriasolucao.appsolucaosistemas.ACAO_EXIBIR_SAUDACAO";
	
	
	private DatabaseHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dadoscliente);
		txtcd_cli = (TextView) findViewById(R.id.txtcd_cli);
		txtnm_cli = (TextView) findViewById(R.id.txtnm_cli);
		txtnr_tel = (TextView) findViewById(R.id.txtnr_tel);
		txtnr_fax = (TextView) findViewById(R.id.txtnr_fax);
		txtnr_cel = (TextView) findViewById(R.id.txtnr_cel);
		 txtnm_rua = (TextView) findViewById(R.id.txtnm_rua);
		 txtnm_rzascl = (TextView) findViewById(R.id.txtnm_rzascl);
		 txtnr_cpfcnpj = (TextView) findViewById(R.id.txtnr_cpfcnpj);
		 txtnr_rgie = (TextView) findViewById(R.id.txtnr_rgie);
		 txtnm_cpm = (TextView) findViewById(R.id.txtnm_cpm);
		 txtnr_cep = (TextView) findViewById(R.id.txtnr_cep);
		 txtds_obs = (TextView) findViewById(R.id.txtds_obs);
		 txtds_email = (TextView) findViewById(R.id.txtds_email);
		 txtnm_brr = (TextView) findViewById(R.id.txtnm_brr);
		 txtds_complemento = (TextView) findViewById(R.id.txtds_complemento);
		 txtcd_uf = (TextView) findViewById(R.id.txtcd_uf);
		
		// prepara acesso ao banco de dados
		helper = new DatabaseHelper(this);
		
		Intent intent = getIntent();
		if (intent.hasExtra(ACAO_EXIBIR_SAUDACAO)) {

			txtcd_cli.setText(intent.getStringExtra(ACAO_EXIBIR_SAUDACAO));

			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.rawQuery(
					"SELECT   _id,cd_cli , nm_cli , nr_tel , nr_fax , nr_cel , nm_rua , nm_rzascl , nr_cpfcnpj , nr_rgie , nm_cpm , nr_cep , ds_obs , ds_email , nm_brr , ds_complemento, cd_uf from cliente where cd_cli="+ txtcd_cli.getText().toString(), null);
			cursor.moveToFirst();
			
			txtcd_cli.setText(cursor.getString(1).toString());
			txtnm_cli.setText(cursor.getString(2).toString());
			txtnr_tel.setText(cursor.getString(3).toString());
			txtnr_fax.setText(cursor.getString(4).toString());
			txtnr_cel.setText(cursor.getString(5).toString());
			txtnm_rua.setText(cursor.getString(6).toString());
			txtnm_rzascl.setText(cursor.getString(7).toString());
			txtnr_cpfcnpj.setText(cursor.getString(8).toString());
			txtnr_rgie.setText(cursor.getString(9).toString());
			txtnm_cpm.setText(cursor.getString(10).toString());
			txtnr_cep.setText(cursor.getString(11).toString());
			txtds_obs.setText(cursor.getString(12).toString());
			txtds_email.setText(cursor.getString(13).toString());
			txtnm_brr.setText(cursor.getString(14).toString());
			txtds_complemento.setText(cursor.getString(15).toString());
			txtcd_uf.setText(cursor.getString(16).toString());
			cursor.close();
		} else {
			txtcd_cli.setText("O nome do usuário não foi informado");
		}

	}
	
	public String configuranumerofone(String fone) // rotina para tirar o 67 oara realizar as ligaões
	{
		
    	if (fone.substring(0,2).toString().equals("67"))
		{		
    		fone = fone.substring(2, fone.length());
		}
		
		if (fone.substring(0,4).toString().equals("(67)"))
		{		
			fone = fone.substring(4, fone.length());
		}
		
		return fone;
	}
	
	public void adicionarligtel(View view)
	{	
		if (txtnr_tel.getText().length() >7)
		{
		Uri call = Uri.parse("tel:"+configuranumerofone(txtnr_tel.getText().toString()));		
		Intent intent = new Intent(Intent.ACTION_CALL,call);
		startActivity(intent);
		}
	}
	
	public void adicionarligfax(View view)
	{	
		if (txtnr_fax.getText().length() >7)
		{
		Uri call = Uri.parse("tel:"+configuranumerofone(txtnr_fax.getText().toString()));		
		Intent intent = new Intent(Intent.ACTION_CALL,call);
		startActivity(intent);
		}
	}
	
	public void adicionarligcel(View view)
	{	
		if (txtnr_cel.getText().length() >7)
		{
		Uri call = Uri.parse("tel:"+configuranumerofone(txtnr_cel.getText().toString()));		
		Intent intent = new Intent(Intent.ACTION_CALL,call);
		startActivity(intent);
		}
	}
	
	public void enviarEmail(View view)
	{
		
		Uri call = Uri.parse("mailto:"+txtds_email.getText());
		Intent intent = new Intent(Intent.ACTION_SENDTO,call);
		startActivity(intent);
	}
	
	public void abrirMapa(View view)
	{
		
Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q="+txtnm_rua.getText()+"+-+"+txtnm_brr.getText()+",+"+txtnm_cpm.getText()+"+-+"+txtcd_uf.getText()));
		
		startActivity(intent);

	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}
