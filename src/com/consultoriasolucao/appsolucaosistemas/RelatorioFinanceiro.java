package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.xml.sax.DTDHandler;

import com.consultoriasolucao.appsolucaosistemas.R.array;
import com.consultoriasolucao.appsolucaosistemas.R.string;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ListActivity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.widget.SimpleAdapter.ViewBinder;

public class RelatorioFinanceiro extends Activity implements
		OnItemClickListener {

	private ListView lista;

	private DatabaseHelper db;
	private List<Map<String, String>> produtos;

	private TextView txtttdespesa;
	private TextView txtttreceita;
	private TextView txtsaldo;
	private String filtro;
	String dtini;
	String dtfim;
	String categoria1;
	String situacao;
	String ds_tipo;
	private Date dt_ini, dt_fim, dt_aux;

	private String strsql;

	public static final String EXTRA_NOME_USUARIO = "AppSolucaoSistemas.EXTRA_NOME_USUARIO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_relatoriofinanceiro);
		lista = (ListView) findViewById(R.id.listarelatfin);
		txtttdespesa = (TextView) findViewById(R.id.txtttdespesa);
		txtttreceita = (TextView) findViewById(R.id.txtttreceita);
		txtsaldo = (TextView) findViewById(R.id.txtsaldo);

		db = new DatabaseHelper(this);

		lista.setOnItemClickListener(this);
		registerForContextMenu(lista);

		Intent intent = getIntent();
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
			categoria1 = st.nextToken();
			situacao = st.nextToken();
			ds_tipo = st.nextToken();
		}
		String de[] = {  "dt_lancamento", "cd_lancamento","vl_lancamento",
				"ds_historico", "ds_categoria" ,"dt_vencimento","ds_situacao" };
		int para[] = { R.id.dt_lancamento, R.id.cd_lancamentolist,
				R.id.vl_lancamento, R.id.ds_historico, R.id.ds_categoria12 , R.id.dt_vencimento, R.id.ds_situacao};

		SimpleAdapter adapter = new SimpleAdapter(this, buscarRelat(dtini,
				dtfim, categoria1, situacao, ds_tipo),
				R.layout.listview_relatfinanceiro, de, para);

		lista.setAdapter(adapter);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_financeiro, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// Toast.makeText(this, item.toString(), Toast.LENGTH_LONG).show();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		String cd_lancamento;

		switch (item.getItemId()) {
		case R.id.remover_despesarec:

			cd_lancamento = produtos.get(info.position).get("cd_lancamento");
			cd_lancamento = cd_lancamento.substring(0,
					cd_lancamento.indexOf("-"));
			produtos.remove(info.position);
			lista.invalidateViews();

			db.getWritableDatabase().execSQL(
					"delete from financas where _id =" + cd_lancamento);

			return true;
		case R.id.realiza_pagamento:

			cd_lancamento = produtos.get(info.position).get("cd_lancamento");
			cd_lancamento = cd_lancamento.substring(0,
					cd_lancamento.indexOf("-"));
			produtos.remove(info.position);
			lista.invalidateViews();

			db.getWritableDatabase().execSQL(
					"update financas  set ds_situacao ='P' where _id ="
							+ cd_lancamento);
			return true;
		
		case R.id.insere_lancamento:

			startActivity(new Intent(this, LancaDespesa.class));
			return true;
			
			
		case R.id.atualiza_lista:

			buscarrelat();
			return true;
			
		case R.id.editar_lancamento:

			Intent intent = new Intent(this, LancaDespesa.class);
			cd_lancamento = produtos.get(info.position).get("cd_lancamento");
			cd_lancamento = cd_lancamento.substring(0,cd_lancamento.indexOf("-"));
			intent.putExtra(LancaDespesa.EXTRA_CD_LANCAMENTO, cd_lancamento);
			startActivity(intent);		
			
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
			String datafim, String categoria, String situacao, String ds_tipo) {
		// buscar todos os produtos do banco

		SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
		dataini = f.format(ConvertToDate(dataini));
		datafim = f.format(ConvertToDate(datafim));

		dt_ini = ConvertToDate(dataini);
		dt_fim = ConvertToDate(datafim);
		strsql = "select a._id,  a.dt_lancamento, a.vl_despesa,a.vl_receita , a.ds_historico, coalesce(b.ds_categoria,''),a.dt_vencimento, a.ds_situacao  from financas a left join categoria b on (a.cd_categoria =b._id)  ";

		if (situacao.substring(0, 1).equals("A")) // caso o usuario queira os
													// titulos em aberto puxa
													// pela data de vencimento
		{
			strsql = strsql + "where a.dt_vencimento between '"
					+ dt_ini.getTime() + "'  and '" + dt_fim.getTime() + "'  ";
		} else
			strsql = strsql + "where a.dt_lancamento between '"
					+ dt_ini.getTime() + "' and  '" + dt_fim.getTime() + "'  ";

		if (!categoria.equals("TODOS")) {
			int posicao = categoria.indexOf("-");
			strsql = strsql + " AND a.cd_categoria ="
					+ categoria.substring(0, posicao);
		}

		if (!situacao.equals("TODOS")) {

			strsql = strsql + " AND coalesce(a.ds_situacao,'P') ='"
					+ situacao.substring(0, 1) + "' ";
		}

		if (!ds_tipo.equals("TODOS")) {

			strsql = strsql + " AND a.ds_tipo ='" + ds_tipo.substring(0, 1)
					+ "' ";
		}

		strsql = strsql + "  order by a.dt_lancamento";

		Cursor c = db.getReadableDatabase().rawQuery(strsql, null);
		produtos = new ArrayList<Map<String, String>>();
		DecimalFormat df = new DecimalFormat(",##0.00");
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");

		while (c.moveToNext()) {
			Map<String, String> mapa = new HashMap<String, String>();

			
			
			
			strsql =c.getString(0);
			mapa.put("cd_lancamento", c.getString(0) + "-");
			
			dt_aux = new Date(c.getLong(1));
			strsql = dateFormat.format(dt_aux);
			mapa.put("dt_lancamento","Data Lanc.: "+ dateFormat.format(dt_aux));
			

			if (c.getDouble(2) > 0) {
				mapa.put("vl_lancamento", "-" + df.format(c.getDouble(2))
						+ "  ");
			} else
				mapa.put("vl_lancamento",  df.format(c.getDouble(3))
						+ "  ");

			mapa.put("ds_historico", c.getString(4).toString());
			mapa.put("ds_categoria", c.getString(5).toString());
			dt_aux = new Date(c.getLong(6));
			if (c.getLong(1) != c.getLong(6)) //caso o vencimento seja diferente da data de lançamento quer dizer que é um lançamento a vencer
			{
			  mapa.put("dt_vencimento",dateFormat.format(dt_aux));
			} else mapa.put("dt_vencimento","");
			
			if (c.getString(7).toString().equals("A"))
			{
				mapa.put("ds_situacao","A Vencer");
			} else mapa.put("ds_situacao","Pago");
			
			produtos.add(mapa);
		}

		c.close();

		strsql = "select sum(vl_despesa) as vl_despesa, sum(vl_receita) as vl_receita    from financas ";

		if (situacao.substring(0, 1).equals("A")) // caso o usuario queira os
													// titulos em aberto puxa
													// pela data de vencimento
		{
			strsql = strsql + "where dt_vencimento between '"
					+ dt_ini.getTime() + "'  and '" + dt_fim.getTime() + "'  ";
		} else
			strsql = strsql + "where dt_lancamento between '"
					+ dt_ini.getTime() + "'  and '" + dt_fim.getTime() + "'  ";

		if (!categoria.equals("TODOS")) {
			int posicao = categoria.indexOf("-");
			strsql = strsql + " AND cd_categoria ="
					+ categoria.substring(0, posicao);
		}

		if (!situacao.equals("TODOS")) {

			strsql = strsql + " AND ds_situacao ='" + situacao.substring(0, 1)
					+ "' ";
		}

		if (!ds_tipo.equals("TODOS")) {

			strsql = strsql + " AND ds_tipo ='" + ds_tipo.substring(0, 1)
					+ "' ";
		}

		Cursor ct = db.getReadableDatabase().rawQuery(strsql, null);
		ct.moveToFirst();

		txtttdespesa.setText(df.format(ct.getDouble(0)));
		txtttreceita.setText(df.format(ct.getDouble(1)));
		txtsaldo.setText(df.format(ct.getDouble(1) - ct.getDouble(0)));
		ct.close();
		// construir objeto de retorno que é uma String[]
		return produtos;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}

}
