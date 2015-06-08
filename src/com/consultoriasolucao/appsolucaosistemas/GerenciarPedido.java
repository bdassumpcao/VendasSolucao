package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class GerenciarPedido extends Activity implements
		OnItemClickListener {

	private ListView lista;

	private DatabaseHelper db;
	private List<Map<String, String>> produtos;

	private TextView txttt_vlpedido;
	private TextView txttt_qtpedido;
	private String filtro;
	String dtini;
	String dtfim;
	String cd_cli;
	private Button btinserirpedido;
	
	private Date dt_ini, dt_fim, dt_aux;

	private String strsql;

	public static final String EXTRA_NOME_USUARIO = "AppSolucaoSistemas.EXTRA_NOME_USUARIO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gerenciarpedido);
		lista = (ListView) findViewById(R.id.listapedido);
		txttt_vlpedido = (TextView) findViewById(R.id.txttt_vlpedido);
		txttt_qtpedido = (TextView) findViewById(R.id.txttt_qtpedido);
		btinserirpedido = (Button) findViewById(R.id.btinserirpedido);
		
		db = new DatabaseHelper(this);

		lista.setOnItemClickListener(this);
		registerForContextMenu(lista);

		Intent intent = getIntent();
		//atualizando a listview conforme os filtros do relatorio
		if (intent.hasExtra(EXTRA_NOME_USUARIO)) {

			filtro = intent.getStringExtra(EXTRA_NOME_USUARIO);
			buscarrelat();

		}	
	

	}

	public void buscarrelat() {
		StringTokenizer st = new StringTokenizer(filtro, "|");
		while (st.hasMoreTokens()) {
			dtini = st.nextToken();
			dtfim = st.nextToken();
			cd_cli = st.nextToken(); 
			
		}
		String de[] = {  "cd_pedido", "nm_cli","dt_lancamento","vl_total"};
		int para[] = { R.id.cd_pedido, R.id.nm_cli,R.id.dt_lancamento, R.id.vl_total};

		SimpleAdapter adapter = new SimpleAdapter(this, buscarRelat(dtini,dtfim, cd_cli), R.layout.listview_pedido, de, para);

		lista.setAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_pedido, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// Toast.makeText(this, item.toString(), Toast.LENGTH_LONG).show();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		String cd_lancamento;

		switch (item.getItemId()) {
			case R.id.remover_pedido:
				cd_lancamento = produtos.get(info.position).get("cd_pedido");			
				produtos.remove(info.position);
				lista.invalidateViews();
				db.getWritableDatabase().execSQL(
						"delete from itenspedido where cd_pedido="+cd_lancamento);
				db.getWritableDatabase().execSQL(
						"delete from pedido where _id="+cd_lancamento);
				buscarrelat();
				return true;				
			
			case R.id.enviar_xmlemail:			
				Intent intent4 = new Intent(this, Enviaremailcliente.class);
				cd_lancamento = produtos.get(info.position).get("cd_pedido");
				intent4.putExtra(Enviaremailcliente.EXTRA_CD_PEDIDO, cd_lancamento);
				startActivity(intent4);	
				
				Intent intent3 = new Intent(this, Enviaremailloja.class);
				cd_lancamento = produtos.get(info.position).get("cd_pedido");
				intent3.putExtra(Enviaremailloja.EXTRA_CD_PEDIDO, cd_lancamento);
				startActivity(intent3);					
				return true;
				
			case R.id.editar_pedido:
				Intent intent1 = new Intent(this, LancaPedido.class);
				cd_lancamento = produtos.get(info.position).get("cd_pedido");
				intent1.putExtra(LancaPedido.EXTRA_CD_PEDIDO, cd_lancamento);
				startActivity(intent1);				
				return true;						
				
			default:
				return super.onContextItemSelected(item);
		}
	}

	private Date ConvertToDate(String dateString) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertedDate;
	}

	private List<Map<String, String>> buscarRelat(String dataini,
			String datafim, String cd_cli) {


		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
		dataini = f.format(ConvertToDate(dataini));
		datafim = f.format(ConvertToDate(datafim));

		dt_ini = ConvertToDate(dataini);
		dt_fim = ConvertToDate(datafim);
		strsql = "select a._id,  a.dt_lancamento, a.vl_total,b.nm_cli,a._id from pedido a join cliente b on (a.cd_cli =b.cd_cli)  ";
			
		strsql = strsql+ "where a.dt_lancamento between '" + dt_ini.getTime() + "'  and '" + dt_fim.getTime() + "'  ";

		
		if (!cd_cli.equals("0")) {
			strsql = strsql + " AND a.cd_cli =" + cd_cli;
		}

		
		strsql = strsql + "  order by a.dt_lancamento";

		Cursor c = db.getReadableDatabase().rawQuery(strsql, null);
		produtos = new ArrayList<Map<String, String>>();
		DecimalFormat df = new DecimalFormat(",##0.00");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

		while (c.moveToNext()) {
			Map<String, String> mapa = new HashMap<String, String>();

			
			
			
			
			mapa.put("cd_pedido", c.getString(4));
			mapa.put("nm_cli", c.getString(3).toString());
			
			dt_aux = new Date(c.getLong(1));			
			mapa.put("dt_lancamento","Data .: "+ dateFormat.format(dt_aux));
			

			mapa.put("vl_total",  df.format(c.getDouble(2))+ " ");
			
			
			produtos.add(mapa);
		}

		c.close();

		strsql = "select sum(vl_total), count(*) from pedido a join cliente b on (a.cd_cli =b._id)  ";
		
		strsql = strsql+ "where a.dt_lancamento between '" + dt_ini.getTime() + "'  and '" + dt_fim.getTime() + "'  ";

		if (!cd_cli.equals("0")) {
			strsql = strsql + " AND a.cd_cli =" + cd_cli;
		}

		Cursor ct = db.getReadableDatabase().rawQuery(strsql, null);
		ct.moveToFirst();

		txttt_vlpedido.setText(df.format(ct.getDouble(0)));
		txttt_qtpedido.setText(df.format(ct.getDouble(1)));		
		
		
		ct.close();
		// construir objeto de retorno que é uma String[]
		return produtos;
	}
	
	public void atualizaLista(View view)
	{
	  this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;

	}

	public void inserirPedido(View view)
	{
		Intent intent = new Intent(this, LancaPedido.class);		
		startActivity(intent);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

	@Override
	public void onResume(){
		super.onResume();
		buscarrelat();
		
		
	}

}
