package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ConsultaProduto extends Activity implements OnItemClickListener {

	private ListView lista;
	private EditText nome;
	private DatabaseHelper helper;
	private List<Map<String, String>> produtos;
	private TextView saudacaoTextView;
	public static final String EXTRA_NOME_USUARIO = "AppSolucaoSistemas.EXTRA_NOME_USUARIO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_consultaproduto);
		lista = (ListView) findViewById(R.id.listaProduto);
		lista.setOnItemClickListener(this);
		nome = (EditText) findViewById(R.id.edtnmproduto);
		helper = new DatabaseHelper(this);

	}

	private List< Map<String, String>> buscarProdutos(String nome) {
		// buscar todos os produtos do banco
		

		Cursor c = helper.getReadableDatabase().rawQuery("select _id,  cd_prd, nm_prd, vl_vnd,rf_prd,qt_prd  from produto where nm_prd like '%"+nome+"%' ORDER BY cd_prd ", null);
		produtos = new ArrayList<Map<String,String>>();
		DecimalFormat df = new DecimalFormat(",##0.00");
		while (c.moveToNext()) 
		{
			Map<String, String> mapa = new HashMap<String,String>();
			mapa.put("cd_prd",  c.getString(1).trim());
			mapa.put("vl_vnd", " R$: " + df.format(c.getDouble(3))+"  ");
			mapa.put("nm_prd", c.getString(2));
			mapa.put("rf_prd", "Ref.: "+ c.getString(4));	
			mapa.put("qt_prd", "Quant.: " +c.getString(5));
			produtos.add(mapa);
		}
		c.close();
		helper.close();
		// construir objeto de retorno que é uma String[]
		return produtos;
	}

	public void buscarproduto(View view) 
	{
		String de[] = { "cd_prd","vl_vnd","nm_prd","rf_prd","qt_prd" };
		int para[] = { R.id.cd_prd, R.id.vl_vnd,R.id.nm_prd,R.id.rf_prd,R.id.qt_prd  };

		SimpleAdapter adapter = new SimpleAdapter(this, buscarProdutos(nome.getEditableText().toString()), R.layout.listview_produto, de, para);

		
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
		Map<String, String> item = produtos.get(posicao);
		this.saudacaoTextView = (TextView) findViewById(R.id.txtcodprod);
		this.saudacaoTextView.setText(item.get("cd_prd"));

		
		if (getIntent().hasExtra(LancaItensPedido.EXTRA_CD_PRD)) //caso a tela de consulta foi chamada pela tela de lançamento de pedido
		{		
			Intent intent = new Intent(this, DadosProduto.class);
			intent.putExtra(LancaItensPedido.EXTRA_CD_PRD, item.get("cd_prd").toString());
			setResult(RESULT_OK, intent);
			finish();
		}else
		{
	    	Intent intent = new Intent(this, DadosProduto.class);
		    intent.putExtra(DadosProduto.EXTRA_NOME_USUARIO, item.get("cd_prd"));
	    	startActivity(intent);
		}
	}

}
