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

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class LancaItensPedido extends Activity implements OnItemClickListener {

	EditText txtqt_iten;
	EditText txtvl_iten;
	TextView txtcd_pedido;
	TextView txtcd_tabelapreco;
	TextView txttt_pedido;
	TextView txtcd_prd;
	TextView txtvl_descontofinal;
	Button btcd_prd;
	String sqltext;

	
	private ListView lista;
	private List<Map<String, String>> produtos;
	
	private DatabaseHelper helper;	
	public static final String EXTRA_CD_PRD = "com.consultoriasolucao.appsolucaosistemas.EXTRA_CD_PRD";
	public static final String EXTRA_CD_PEDIDO = "com.consultoriasolucao.appsolucaosistemas.EXTRA_CD_PEDIDO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lancaitenspedido);
		helper = new DatabaseHelper(this);

		this.txtqt_iten = (EditText) findViewById(R.id.edtqt_iten);
		this.txtvl_iten = (EditText) findViewById(R.id.edtvl_iten);
		this.txtcd_pedido = (TextView) findViewById(R.id.txtcd_pedido);
		this.txttt_pedido = (TextView) findViewById(R.id.txttt_pedido);
		this.txtcd_tabelapreco = (TextView) findViewById(R.id.txtcd_tabelapreco);
		this.txtcd_prd =  (TextView) findViewById(R.id.txtcd_prd);
		this.btcd_prd = (Button) findViewById(R.id.btcd_prd);
		this.txtvl_descontofinal = (TextView) findViewById(R.id.txtvl_descontofinal);
		this.lista = (ListView) findViewById(R.id.listaitenspedido);

	
		lista = (ListView) findViewById(R.id.listaitenspedido);
		registerForContextMenu(lista);

		Intent intent = getIntent();
		if (intent.hasExtra(EXTRA_CD_PEDIDO)) 
		{//caso seja edição então carregando os campos
			
			
			
			
			txtcd_pedido.setText(intent.getStringExtra(EXTRA_CD_PEDIDO));			
			buscapedido(txtcd_pedido.getText().toString());
			
			
			SQLiteDatabase dbexe = helper.getReadableDatabase();
			Cursor cursor = dbexe.rawQuery("SELECT  coalesce(cd_tabelapreco,0),vl_total,vl_desconto from pedido where _id="+  txtcd_pedido.getText().toString(), null);
			cursor.moveToFirst();
			
			
			txtcd_tabelapreco.setText(cursor.getString(0).toString());
			txtvl_descontofinal.setText(cursor.getString(2).toString());
			txttt_pedido.setText(cursor.getString(1).toString());
			
			}		
		
	}


	public void consultaProduto(View view) { //Procedimenot para chamar a tela de consulta produto
		if (txtcd_prd.getText().toString().equals("")) //caso não seja consulta por código
		{
		Intent intent = new Intent(this, ConsultaProduto.class);
		intent.putExtra(EXTRA_CD_PRD, true);
		startActivityForResult(intent, 1);	
		}else
		{
			SQLiteDatabase db = helper.getReadableDatabase();
			if (txtcd_tabelapreco.getText().toString().equals("0")) //caso não tenha tabela de preço
			{
				sqltext = "SELECT  cd_prd, nm_prd, vl_vnd from produto where cd_prd="+  txtcd_prd.getText().toString();
			} else
			{
				sqltext = "SELECT  a.cd_prd, a.nm_prd, b.vl_vnd from produto a join tabelaprecoprd b on (a.cd_prd=b.cd_prd) where a.cd_prd="+  txtcd_prd.getText().toString()+" and b.cd_tabelapreco="+txtcd_tabelapreco.getText().toString();
				
			}
			Cursor cursor = db.rawQuery(sqltext, null);
			cursor.moveToFirst();
			
			btcd_prd.setText(cursor.getString(1).toString());
			txtvl_iten.setText(cursor.getString(2).toString().replace(".", ","));	
		}
		

	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK)
		{
					 
			txtcd_prd.setText(data.getStringExtra(EXTRA_CD_PRD));
			SQLiteDatabase db = helper.getReadableDatabase();
			if (txtcd_tabelapreco.getText().toString().equals("0")) //caso não tenha tabela de preço
			{
				sqltext = "SELECT  cd_prd, nm_prd, vl_vnd from produto where cd_prd="+  txtcd_prd.getText().toString();
			} else
			{
				sqltext = "SELECT  a.cd_prd, a.nm_prd, b.vl_vnd from produto a join tabelaprecoprd b on (a.cd_prd=b.cd_prd) where a.cd_prd="+  txtcd_prd.getText().toString()+" and b.cd_tabelapreco="+txtcd_tabelapreco.getText().toString();
				
			}
			Cursor cursor = db.rawQuery(sqltext, null);
			cursor.moveToFirst();
			btcd_prd.setText(cursor.getString(1).toString());
			txtvl_iten.setText(cursor.getString(2).toString().replace(".", ","));
			
			
			
		}
		
	}

	

	public void inserirIten(View view) {

		Boolean flagvalida = true;
		

		if (txtcd_prd.getText().toString().equals("")) {
			Toast.makeText(this, "Entre com o item!", Toast.LENGTH_SHORT)
					.show();
			flagvalida = false;
		}
		if (txtqt_iten.getText().toString().equals("")) {
			Toast.makeText(this, "Entre com a quantidade!", Toast.LENGTH_SHORT)
					.show();
			flagvalida = false;
		}

		if (flagvalida) {
			SQLiteDatabase db = helper.getWritableDatabase();

			helper = new DatabaseHelper(this);
			
			String codbarras="";
			SQLiteDatabase d = helper.getReadableDatabase();
			Cursor cursor = d.rawQuery("SELECT codbarras FROM produto where cd_prd="+txtcd_prd.getText().toString(),	null);
			cursor.moveToNext();
			
			if (cursor.getCount() !=0)
			{		
			  codbarras = cursor.getString(0);
		      cursor.close();
			}
			
			
			// inserindo os itens
			ContentValues values1 = new ContentValues();
			values1.put("cd_pedido", txtcd_pedido.getText().toString());
			values1.put("cd_prd", txtcd_prd.getText().toString());
			values1.put("qt_iten",
					Double.valueOf(txtqt_iten.getText().toString())
							.doubleValue());
			values1.put("vl_iten",
					Double.valueOf(txtvl_iten.getText().toString().replace(",", "."))
							.doubleValue());
			values1.put("codbarras", codbarras);
			long resultado = db.insert("itenspedido", null, values1);

			buscaritenspedido(txtcd_pedido.getText().toString());
			

			limparitens();
			
			
		}// fim if flagvalida
		atualizavalorespedido(txtcd_pedido.getText().toString());
	}

	public void limparitens()
	{
		txtcd_prd.setText("");
		btcd_prd.setText("Produto");
		txtqt_iten.setText("");
		txtvl_iten.setText("");
	}
	public void atualizavalorespedido(String cd_pedido) {
		Cursor c = helper.getReadableDatabase().rawQuery(
				"select coalesce(sum(vl_iten*qt_iten),0) from itenspedido  where cd_pedido= "
						+ cd_pedido, null);
		c.moveToFirst();
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		if (txtvl_descontofinal.getText().toString().equals(""))
		{
			txtvl_descontofinal.setText("0");
		}
		values.put("vl_total", c.getDouble(0)-Double.valueOf(txtvl_descontofinal.getText().toString()).doubleValue());
		values.put("vl_desconto", Double.valueOf(txtvl_descontofinal.getText().toString().replace(",", ".")).doubleValue());
		
		DecimalFormat df = new DecimalFormat(",##0.00");
		txttt_pedido.setText(df.format(c.getDouble(0)-Double.valueOf(txtvl_descontofinal.getText().toString()).doubleValue())+"");
		
		
		long resultado = db.update("pedido", values, "_id = ?",
		new String[] { cd_pedido });	
		
	}

	public void buscapedido(String cd_pedido) {
		buscaritenspedido(cd_pedido);
	}
	

	private List<Map<String, String>> buscaritensPedido(String cd_pedido) {
		// buscar todos os produtos do banco

		Cursor c = helper
				.getReadableDatabase()
				.rawQuery(
						"select a._id,  a.cd_prd, b.nm_prd, a.qt_iten ,a.vl_iten,(a.qt_iten*a.vl_iten) from itenspedido a join produto b on (a.cd_prd=b.cd_prd) where a.cd_pedido= "
								+ cd_pedido, null);
		produtos = new ArrayList<Map<String, String>>();
		DecimalFormat df = new DecimalFormat(",##0.00");
		while (c.moveToNext()) {
			Map<String, String> mapa = new HashMap<String, String>();
			mapa.put("cd_prd", c.getString(1));
			mapa.put("iditenpedido", c.getString(0));
			mapa.put("qt_iten", " Quant.: " + df.format(c.getDouble(3)) + "  ");
			mapa.put("nm_prd", c.getString(2));
			mapa.put("vl_iten", "V.Uni.: " + df.format(c.getDouble(4)));
			mapa.put("vl_total", "Total.: " + df.format(c.getDouble(5)) + " ");
			produtos.add(mapa);
		}
		c.close();
		// construir objeto de retorno que é uma String[]
		return produtos;
	}

	public void buscaritenspedido(String cd_pedido) {
		String de[] = { "cd_prd", "iditenpedido", "qt_iten", "nm_prd",
				"vl_iten", "vl_total" };
		int para[] = { R.id.cd_prd, R.id.iditenpedido, R.id.qt_iten,
				R.id.nm_prd, R.id.vl_iten, R.id.vl_total };

		SimpleAdapter adapter = new SimpleAdapter(this,
				buscaritensPedido(cd_pedido), R.layout.listview_itenspedido,
				de, para);

		lista.setAdapter(adapter);

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

	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// Toast.makeText(this, item.toString(), Toast.LENGTH_LONG).show();
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		String cd_lancamento;

		switch (item.getItemId()) {
		case R.id.remover_itenpedido:

			cd_lancamento = produtos.get(info.position).get("iditenpedido");

			produtos.remove(info.position);
			lista.invalidateViews();

			helper.getWritableDatabase().execSQL(
					"delete from itenspedido where _id =" + cd_lancamento);

			atualizavalorespedido(txtcd_pedido.getText().toString());
			buscaritenspedido(txtcd_pedido.getText().toString());
			return true;

		default:
			return super.onContextItemSelected(item);
		}
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_itenspedido, menu);
		
	}

	
		
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;

	}
	

}
