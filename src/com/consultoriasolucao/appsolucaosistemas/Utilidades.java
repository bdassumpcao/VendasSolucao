package com.consultoriasolucao.appsolucaosistemas;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;


public class Utilidades extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_utilidades);
		
		
		
	}

	public void calcula13sal(View view)
	{
		Uri uri = Uri.parse("http://www.calculador.com.br/calculo/decimo-terceiro");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	public void calcularescisao(View view)
	{
		Uri uri = Uri.parse("http://www.calculador.com.br/calculo/rescisao-clt");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);	
	}
	
	public void calculaFerias(View view)
	{
		Uri uri = Uri.parse("http://www.calculador.com.br/calculo/ferias");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}

	
	public void calculaFinanciamento(View view)
	{
		Uri uri = Uri.parse("http://www.calculador.com.br/calculo/financiamento-price");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}
