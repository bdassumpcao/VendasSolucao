package com.consultoriasolucao.appsolucaosistemas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class SolucaoSistemas extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_solucao_sistemas);
		startService(new  Intent(this, BackgroundService.class));
		
	}
	
//	public void abrirSuporte(View view){
//		startActivity(new Intent(this, Suporte.class));
//	}
//	
//	
//	public void TransacoesOn(View view){
//		startActivity(new Intent(this, TransacoesOn.class));
//	}
//	
//	public void QuemSomos(View view){
//		startActivity(new Intent(this, QuemSomos.class));
//	}
//	
//	public void Produtos(View view){
//		startActivity(new Intent(this, Produtos.class));
//	}
//	
//	public void Contato(View view){
//		startActivity(new Intent(this, Contato.class));
//	}
//	
//	public void Utilidades(View view){
//	  startActivity(new Intent(this, Utilidades.class));
//	}
//
//	public void financasPessoais(View view)
//	{
//		startActivity(new Intent(this,FinancasPessoais.class));
//	}
//	
//	public void siganoFace(View view){
//
//		Uri uri = Uri.parse("https://www.facebook.com/solucaosistemaconsultoriaemsoftware?ref=ts&fref=ts");
//		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//		startActivity(intent);
//		
//		}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}
