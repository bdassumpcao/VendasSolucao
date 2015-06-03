package com.consultoriasolucao.appsolucaosistemas;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

public class Contato extends Activity {

	private WebView webview;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contato);				
		
		
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}
	
	public void iniciarsite(View view)
	{
		Uri uri = Uri.parse("http://www.consultoriasolucao.com");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	
	}
	
	public void iniciarSuporte(View view)
	{
		Uri uri = Uri.parse("https://consultoriasolucao.webchatlw.com.br/clients/start");
		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
		startActivity(intent);
	
	}
	
	public void enviarEmail(View view)
	{
		Uri call = Uri.parse("mailto:contato@consultoriasolucao.com");
		Intent intent = new Intent(Intent.ACTION_SENDTO,call);
		startActivity(intent);
	}
	
	public void abrirMapa(View view)
	{
		
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("google.navigation:q=R.+Antero+Ribeiro,+121+-+Planalto,+Campo+Grande+-+MS&hl=pt-BR&sll=-20.875572,-54.261694&sspn=1.560249,2.705383&oq=RUa+antero+ribeiro,+121&hnear=R.+Antero+Ribeiro,+121+-+Planalto,+Campo+Grande+-+Mato+Grosso+do+Sul,+79009-210&t=m&z=16"));
		
		startActivity(intent);
	}
	
	
	public void acionarLigacao(View view)
	{
		Uri call = Uri.parse("tel:33068495");
		Intent intent = new Intent(Intent.ACTION_CALL,call);
		startActivity(intent);
	}

}
