package com.consultoriasolucao.appsolucaosistemas;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;


public class QuemSomos extends Activity {

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_quemsomos);
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}
