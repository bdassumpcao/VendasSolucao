package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class ConsultaCliente extends Activity  implements OnItemClickListener {

	

	private ListView lista;
	private EditText nome;
	private DatabaseHelper helper;
	
	private List<Map<String, String>> clientes;
	
	public static final String ACAO_EXIBIR_SAUDACAO = "com.consultoriasolucao.appsolucaosistemas.ACAO_EXIBIR_SAUDACAO";
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consultacliente);
		lista = (ListView) findViewById(R.id.listacliente);
	
		nome =  (EditText) findViewById(R.id.edtnmcliente);
		lista.setOnItemClickListener(this);
		helper = new DatabaseHelper(this);
				
		
		
	}
	
	private List< Map<String, String>> buscarcliente(String nome) {
	

		Cursor c = helper.getReadableDatabase().rawQuery("select _id,  cd_cli, nm_cli,nm_rzascl  from cliente where nm_cli like '%" + nome +"%'", null);
		clientes = new ArrayList<Map<String,String>>();
		
		while (c.moveToNext()) 
		{
			Map<String, String> mapa = new HashMap<String,String>();
			mapa.put("cd_cli",   c.getString(1).trim());			
			mapa.put("nm_cli", c.getString(2));			
			mapa.put("nm_rzascl", c.getString(3));
			clientes.add(mapa);
		}
		c.close();
	
		return clientes;
	}

	public void buscarcliente(View view) {
		String de[] = { "cd_cli","nm_cli","nm_rzascl" };
		int para[] = { R.id.cd_cli,R.id.nm_cli, R.id.nm_rzascl  };

		SimpleAdapter adapter = new SimpleAdapter(this, buscarcliente(nome.getEditableText().toString()), R.layout.listview_cliente, de, para);

		
		lista.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int posicao,
			long arg3) {
		Map<String, String> item = clientes.get(posicao);
		
		if (getIntent().hasExtra(LancaPedido.EXTRA_CD_CLI))
		{			
			Intent intent = new Intent(this, DadosCliente.class);
			intent.putExtra(LancaPedido.EXTRA_CD_CLI, item.get("cd_cli").toString());
			setResult(RESULT_OK, intent);
			finish();
		}
		else if (getIntent().hasExtra(FiltroGerenciarPedido.EXTRA_CD_CLI))
		{			
			Intent intent = new Intent(this, DadosCliente.class);
			intent.putExtra(FiltroGerenciarPedido.EXTRA_CD_CLI, item.get("cd_cli").toString());
			setResult(RESULT_OK, intent);
			finish();
		}		
		else
		{
		Intent intent = new Intent(this, DadosCliente.class);
		intent.putExtra(DadosCliente.ACAO_EXIBIR_SAUDACAO, item.get("cd_cli").trim());
		startActivity(intent);
		}
	}

	
}
