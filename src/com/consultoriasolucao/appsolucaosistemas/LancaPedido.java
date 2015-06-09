package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LancaPedido extends Activity implements OnItemClickListener {
	private final String LOG = "vendas";
	private CheckBox cbConferir;
	private TextView txtcd_pedido;
	private TextView txttt_pedido;
	private TextView txtcd_cli;
	private TextView txtds_obs;
	private TextView txtcd_tabelapreco;
	private ListView listprd;
	private EditText edt_descricao;
	private EditText edt_id;
	private EditText edt_desconto;
	private Spinner spnds_formapgto;
	public DecimalFormat df = new DecimalFormat(",##0.00");
	
	private Button btcd_cli;
	private Integer dia, mes, ano;
	private Date dt_lancamento;
	private DatabaseHelper helper;
	private ArrayList<HashMap<String, String>> produtos;
	private CustomAdapter adapter;
	public static final String EXTRA_CD_CLI = "com.consultoriasolucao.appsolucaosistemas.EXTRA_CD_CLI";
	public static final String EXTRA_CD_PEDIDO = "com.consultoriasolucao.appsolucaosistemas.EXTRA_CD_PEDIDO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lancapedido);
		
        //para o teclado não aparecer automaticamente
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); 
		
		helper = new DatabaseHelper(this);
		
		this.edt_desconto = (EditText) findViewById(R.id.edt_desconto);
		this.edt_desconto.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
		this.edt_descricao = (EditText) findViewById(R.id.edt_descricao);
		this.edt_id = (EditText) findViewById(R.id.edt_id);
		this.txtcd_pedido = (TextView) findViewById(R.id.txtcd_pedido);
		this.txttt_pedido = (TextView) findViewById(R.id.txttt_pedido);
		this.txtds_obs = (TextView) findViewById(R.id.txtds_obs);
		this.txtcd_cli = (TextView) findViewById(R.id.txtcd_cli);
		this.txtcd_tabelapreco = (TextView) findViewById(R.id.edtcd_tabelapreco);
		this.btcd_cli = (Button) findViewById(R.id.btcd_cli); 
		this.cbConferir = (CheckBox) findViewById(R.id.cbConferir);
		this.listprd = (ListView)findViewById(R.id.lst_produtos);
		listprd.setItemsCanFocus(true);
		registerForContextMenu(listprd);

		ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.ds_formapgto,android.R.layout.simple_spinner_item);
		spnds_formapgto = (Spinner) findViewById(R.id.spnds_formapgto);
		spnds_formapgto.setAdapter(adapter1);				 		
		
		Calendar calendar = Calendar.getInstance();
		ano = calendar.get(Calendar.YEAR);
		mes = calendar.get(Calendar.MONTH);
		dia = calendar.get(Calendar.DAY_OF_MONTH);		

		Intent intent = getIntent();
		if (intent.hasExtra(EXTRA_CD_PEDIDO)) 
		{//caso seja edição então carregando os campos			
			
			SQLiteDatabase dbexe = helper.getReadableDatabase();
			Cursor cursor = dbexe.rawQuery(
					"SELECT _id, cd_cli, dt_lancamento, vl_total,ds_obs, ds_formapgto, cd_tabelapreco,vl_desconto from pedido where _id="+ intent.getStringExtra(EXTRA_CD_PEDIDO), null);
			cursor.moveToNext();
			txtcd_pedido.setText(cursor.getString(0));
			txtds_obs.setText(cursor.getString(4));
			edt_desconto.setText(cursor.getString(7));
			txtcd_cli.setText(cursor.getString(1));
			txtcd_tabelapreco.setText(cursor.getString(6).toString());
			
			//verificando qual item do sppiner foi selecinado									 	
			int spinnerPosition = adapter1.getPosition(cursor.getString(5));
			spnds_formapgto.setSelection(spinnerPosition);			
			
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor1 = db.rawQuery("SELECT  cd_cli, nm_cli from cliente where cd_cli="+  txtcd_cli.getText().toString(), null);
			cursor1.moveToFirst();
			btcd_cli.setText(cursor1.getString(1).toString());
			
			cursor1.close();
			
			atualizavalorespedido(txtcd_pedido.getText().toString());
			buscarprodutos();			
				
			}	
		buscarprodutos();
		
	}

	@SuppressWarnings("deprecation")
	public void selecionarDataLancamento(View view) {
		showDialog(view.getId());
	}


	public void consultaCliente(View view) {
		if (txtcd_cli.getText().toString().equals("") ) //caso não seja consulta de cliente por código
		{
		Intent intent = new Intent(this, ConsultaCliente.class);
		intent.putExtra(EXTRA_CD_CLI, true);
		startActivityForResult(intent, 1);
		}
		else
		{
			SQLiteDatabase db = helper.getReadableDatabase();
			
			Cursor cursor = db.rawQuery("SELECT  cd_cli, nm_cli from cliente where cd_cli="+  txtcd_cli.getText().toString(), null);
			cursor.moveToFirst();
			
			btcd_cli.setText(cursor.getString(1).toString());				
		}	
		
	}

	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK)
		{
			txtcd_cli.setText(data.getStringExtra(EXTRA_CD_CLI));
			SQLiteDatabase db = helper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT  cd_cli, nm_cli from cliente where cd_cli="+  txtcd_cli.getText().toString(), null);
			cursor.moveToFirst();
			btcd_cli.setText(cursor.getString(1).toString());		
			
		}
		
	}
	
	public void Conferir(View view) {
		if(!txtcd_pedido.getText().toString().equals("")){
			if(cbConferir.isChecked())
				buscaritenspedido(txtcd_pedido.getText().toString());
			else if(!cbConferir.isChecked())
				buscarprodutos();
		}
	}

	
	private ArrayList<HashMap<String, String>> buscaritensPedido(String cd_pedido) {
		// buscar todos os produtos do banco
		Cursor c = helper
				.getReadableDatabase()
				.rawQuery(
						"select a._id,  a.cd_prd, b.nm_prd, a.qt_iten ,a.vl_iten,(a.qt_iten*a.vl_iten) from itenspedido a join produto b on (a.cd_prd=b.cd_prd) where a.cd_pedido= "
								+ cd_pedido, null);
		produtos = new ArrayList<HashMap<String, String>>();
		while (c.moveToNext()) {
			HashMap<String, String> mapa = new HashMap<String, String>();
			mapa.put("cd_prd", c.getString(1));
			mapa.put("iditenpedido", c.getString(0));
			mapa.put("qt_prd", df.format(c.getDouble(3)) + "");
			mapa.put("nm_prd", c.getString(2));
			mapa.put("vl_vnd", df.format(c.getDouble(4)));
			mapa.put("vl_total", "Total R$:\n" + df.format(c.getDouble(5)) + "\n");
			produtos.add(mapa);
		}
		c.close();
		// construir objeto de retorno que é uma String[]
//		Log.i(LOG, "ArrayList<HashMap<String, String>> buscaritenspedido(cd_pedido)");
		return produtos;
	}
	
	
	public void buscaritenspedido(String cd_pedido) {
//		Log.i(LOG, "buscaritenspedido(cd_pedido)");
		adapter = new CustomAdapter(this, R.layout.listview_produtospedido, buscaritensPedido(cd_pedido));
		listprd.setAdapter(adapter);
	}
	
	
	private ArrayList<HashMap<String, String>> buscarProdutos(String nome) {
		// buscar todos os produtos do banco
		if (edt_id.getText().toString().equals("")) //caso não seja consulta por código
		{
			Cursor c = helper.getReadableDatabase().rawQuery("select _id,  cd_prd, nm_prd, vl_vnd,rf_prd,qt_prd  from produto where nm_prd like '%"+nome+"%' ORDER BY nm_prd ", null);
			produtos = new ArrayList<HashMap<String,String>>();
			while (c.moveToNext()) 
			{
				HashMap<String, String> mapa = new HashMap<String,String>();
				mapa.put("cd_prd", c.getString(1).trim());
				mapa.put("nm_prd", c.getString(2));
				mapa.put("qt_prd", df.format(Double.parseDouble("0.00"))+"");
				mapa.put("vl_vnd", df.format(c.getDouble(3))+"");
				mapa.put("vl_total", "");
				produtos.add(mapa);
			}
			c.close();
			helper.close();
			// construir objeto de retorno que é uma String[]
			return produtos;
		}else
		{
			Cursor c = helper.getReadableDatabase().rawQuery("select _id,  cd_prd, nm_prd, vl_vnd,rf_prd,qt_prd  from produto where cd_prd="+edt_id.getText().toString(), null);
			produtos = new ArrayList<HashMap<String,String>>();
			while (c.moveToNext()) 
			{
				HashMap<String, String> mapa = new HashMap<String,String>();
				mapa.put("cd_prd", c.getString(1).trim());
				mapa.put("nm_prd", c.getString(2));
				mapa.put("qt_prd", df.format(Double.parseDouble("0.00"))+"");
				mapa.put("vl_vnd", df.format(c.getDouble(3))+"");
				mapa.put("vl_total", "");
				edt_descricao.setText(c.getString(2));
				produtos.add(mapa);
			}
			c.close();
			helper.close();
			// construir objeto de retorno que é uma String[]
			return produtos;
		}

	}
	
	
	public void buscarprodutos() {
		adapter = new CustomAdapter(this, R.layout.listview_produtospedido, buscarProdutos(edt_descricao.getText().toString()));
		listprd.setAdapter(adapter);
	}
	
	public void buscarprodutos(View view) {
		if(!cbConferir.isChecked()){
			adapter = new CustomAdapter(this, R.layout.listview_produtospedido, buscarProdutos(edt_descricao.getText().toString()));
			listprd.setAdapter(adapter);
		}
	}
	

	public void salvaPedido(View view)
	{
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		
		Boolean flagvalida = true;
		if (btcd_cli.getText().toString().equals("Cliente")) {
			Toast.makeText(this, "Entre com o cliente!", Toast.LENGTH_SHORT)
					.show();
			flagvalida = false;
		}

		if (flagvalida) {
			SQLiteDatabase db = helper.getWritableDatabase();	
			
			
			// primeiro tem que verificar se ja foi inserido o registro do
			// pedido
			
				ContentValues values = new ContentValues();
				dt_lancamento = ConvertToDate(dia+"/"+(mes+1)+"/"+ano);
				values.put("dt_lancamento", dt_lancamento.getTime());
				values.put("vl_bruto", 0);
				if(edt_desconto.getText().toString().equals("")){
					values.put("vl_desconto", 0);			
					edt_desconto.setText("0");
				}
				else
				values.put("vl_desconto", edt_desconto.getText().toString().replace(",", "."));
				values.put("vl_total", 0);
				values.put("cd_cli", txtcd_cli.getText().toString());
				values.put("ds_obs", txtds_obs.getText().toString());
				values.put("ds_formapgto", spnds_formapgto.getSelectedItem().toString());
				values.put("cd_tabelapreco", txtcd_tabelapreco.getText().toString());
										
				if (txtcd_pedido.getText().equals("")) {
				long resultado = db.insert("pedido", null, values);
				txtcd_pedido.setText(resultado+"");
				} else db.update("pedido",  values, "_id ="+txtcd_pedido.getText().toString(),null);
				
				
				
//				btsalvaPedido.setVisibility(View.INVISIBLE);
		
			atualizavalorespedido(txtcd_pedido.getText().toString());
			
		}// fim if flagvalida
		
		
		 
		//se Conferir não estiver clicado então insere produtos no pedido
		int x = listprd.getAdapter().getCount();
		if(!cbConferir.isChecked()){	
			int j=0;
			
			String cd_Alterado[] = new String[100];
			String nm_Alterado[] = new String[100];
			String qt_Alterado[] = new String[100];
			String vl_Alterado[] = new String[100];
			
			for(int i=0; i<x; i++){
				String cd_prd = "", nm_prd="", qt_prd="", vl_vnd="";
	
				@SuppressWarnings("unchecked")
				HashMap<String,String> obj = (HashMap<String,String>) listprd.getAdapter().getItem(i);
				cd_prd = obj.get("cd_prd");
				nm_prd = obj.get("nm_prd");
				qt_prd = obj.get("qt_prd");
				vl_vnd = obj.get("vl_vnd");
//				vl_total = obj.get("vl_total");
				
			
				
				if(!qt_prd.equals("") & !qt_prd.equals("0,00") & !qt_prd.equals("0.00") & !qt_prd.equals("0") & !qt_prd.equals("0.0") & !qt_prd.equals("0,0")){												
					cd_Alterado[j] = cd_prd;
					nm_Alterado[j] = nm_prd;
					qt_Alterado[j] = qt_prd;
					vl_Alterado[j] = vl_vnd;
					j++;							
				}
			}
			
			for(int y=0; y<cd_Alterado.length; y++){
				if(cd_Alterado[y] != null){
					String codbarras="";
					SQLiteDatabase d = helper.getReadableDatabase();
					Cursor cursor = d.rawQuery("SELECT codbarras FROM produto where cd_prd="+cd_Alterado[y], null);
					cursor.moveToNext();
					
					if (cursor.getCount() !=0)
					{		
					  codbarras = cursor.getString(0);
				      cursor.close();
					}
					d.close();
					
					ContentValues values = new ContentValues();
					values.put("cd_pedido", txtcd_pedido.getText().toString());
					values.put("cd_prd", cd_Alterado[y]);
					values.put("qt_iten", qt_Alterado[y].replace(",", "."));
					values.put("vl_iten", vl_Alterado[y].replace(",", "."));
					values.put("codbarras", codbarras);
					SQLiteDatabase db = helper.getWritableDatabase();
					db.insert("itenspedido", null, values);
					db.close();
					Log.i(LOG, "\n INSERE y="+ y + " cd_prd=" + cd_Alterado[y] + " nm_prd=" + nm_Alterado[y] + " qt_prd= " + qt_Alterado[y] + " vl_vnd= " + vl_Alterado[y] );
					buscarprodutos();
					atualizavalorespedido(txtcd_pedido.getText().toString());
				}
			}
		}
		
		else if(cbConferir.isChecked()){
			Boolean alterado = false;
		
			int j=0;
			String id_Alterado[] = new String[100];
			String cd_Alterado[] = new String[100];
			String nm_Alterado[] = new String[100];
			String qt_Alterado[] = new String[100];
			String vl_Alterado[] = new String[100];
			String index_Alterado[] = new String[100];
			for(int i=0; i<x; i++){
				
				String cd_prd = "", iditenpedido="", nm_prd="", qt_prd="", vl_vnd="";
				@SuppressWarnings("unchecked")
				HashMap<String,String> obj = (HashMap<String,String>) listprd.getAdapter().getItem(i);
				cd_prd = obj.get("cd_prd");
				iditenpedido = obj.get("iditenpedido");
				nm_prd = obj.get("nm_prd");
				qt_prd = obj.get("qt_prd");
				vl_vnd = obj.get("vl_vnd");
//				vl_total = obj.get("vl_total");			
				
				
				Cursor c = helper.getReadableDatabase().rawQuery("select a._id,  a.cd_prd, b.nm_prd, a.qt_iten ,a.vl_iten,(a.qt_iten*a.vl_iten) from itenspedido a join produto b on (a.cd_prd=b.cd_prd) where a.cd_pedido=" +txtcd_pedido.getText().toString()+" and a.cd_prd="+cd_prd, null);
				while (c.moveToNext()) 
				{
					String q = df.format(c.getDouble(3));
					String v = df.format(c.getDouble(4));

					if(!qt_prd.equals(q) | !vl_vnd.equals(v)){
						alterado = true;
						id_Alterado[j] = iditenpedido;
						cd_Alterado[j] = cd_prd;
						nm_Alterado[j] = nm_prd;
						qt_Alterado[j] = qt_prd;
						vl_Alterado[j] = vl_vnd;
						index_Alterado[j] = i+"";
						j++;
					}
				c.close();
				}
			}
			
			if(alterado){
				int aux=0; //quando excluiu uma posição o auxiliar aumenta para que a proxima exclusão não seja no mesmo indice
				for(int y=0; y<id_Alterado.length; y++){
					if(id_Alterado[y]!=null){
						if(qt_Alterado[y].equals("0")){
							
							produtos.remove(Integer.parseInt(index_Alterado[y])-aux);
							Log.i(LOG, "produtos.remove("+index_Alterado[y]+")");
							
							listprd.invalidateViews();
							helper.getWritableDatabase().execSQL(
									"delete from itenspedido where _id =" + id_Alterado[y]);	
							Log.i(LOG, "delete from itenspedido where _id ="+id_Alterado[y]);	
							
							id_Alterado[y]=null;
							y=0;
							
							aux++;
							atualizavalorespedido(txtcd_pedido.getText().toString());
						}
						else{
							String codbarras="";
							SQLiteDatabase d = helper.getReadableDatabase();
							Cursor cursor = d.rawQuery("SELECT codbarras FROM produto where cd_prd="+cd_Alterado[y], null);
							cursor.moveToNext();
							
							if (cursor.getCount() !=0)
							{		
							  codbarras = cursor.getString(0);
						      cursor.close();
							}
							d.close();
							
							ContentValues values = new ContentValues();
							values.put("cd_pedido", txtcd_pedido.getText().toString());
							values.put("cd_prd", cd_Alterado[y]);
							values.put("qt_iten", qt_Alterado[y].replace(",", "."));
							values.put("vl_iten", vl_Alterado[y].replace(",", "."));
							values.put("codbarras", codbarras);
							SQLiteDatabase db = helper.getWritableDatabase();
							db.update("itenspedido", values, "_id ="+id_Alterado[y], null);
							db.close();
							atualizavalorespedido(txtcd_pedido.getText().toString());
							buscaritenspedido(txtcd_pedido.getText().toString());
						}
					}
				}
			}
		}		
	}

	
	
	public void atualizavalorespedido(String cd_pedido) {
		Cursor c = helper.getReadableDatabase().rawQuery(
				"select coalesce(sum(vl_iten*qt_iten),0)  from itenspedido  where cd_pedido= "
						+ cd_pedido, null);
		c.moveToFirst();
		
		SQLiteDatabase db = helper.getWritableDatabase();
		ContentValues values = new ContentValues();
		double vl_total = c.getDouble(0) - Double.parseDouble(edt_desconto.getText().toString());
		values.put("vl_total", vl_total);
		db.update("pedido",  values, "_id ="+cd_pedido,null);
		db.close();
		DecimalFormat df = new DecimalFormat(",##0.00");
		txttt_pedido.setText(df.format(vl_total)+"");		
	}



	@SuppressLint("SimpleDateFormat")
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

	

//	@Override
//	public void onCreateContextMenu(ContextMenu menu, View v,
//			ContextMenuInfo menuInfo) {
//		MenuInflater inflater = getMenuInflater();
//		inflater.inflate(R.menu.menu_itenspedido, menu);
//		
//	}
	
//	@Override
//	public boolean onContextItemSelected(MenuItem item) {
//		// Toast.makeText(this, item.toString(), Toast.LENGTH_LONG).show();
//		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
//				.getMenuInfo();
//		String cd_lancamento;
//
//		switch (item.getItemId()) {
//		case R.id.remover_itenpedido:
//
//			cd_lancamento = "";
//			String cd_prd = produtos.get(info.position).get("cd_prd");
//			
//			SQLiteDatabase d = helper.getReadableDatabase();
//			Cursor cursor = d.rawQuery("SELECT _id FROM itenspedido where cd_prd="+cd_prd, null);
//			cursor.moveToNext();
//			
//			if (cursor.getCount() !=0)
//			{		
//			  cd_lancamento = cursor.getString(0);
//		      cursor.close();
//			}
//			
//
//			produtos.remove(info.position);
//			listprd.invalidateViews();
//
//			helper.getWritableDatabase().execSQL(
//					"delete from itenspedido where _id =" + cd_lancamento);
//
//			atualizavalorespedido(txtcd_pedido.getText().toString());
//			buscaritenspedido(txtcd_pedido.getText().toString());
//			return true;
//
//		default:
//			return super.onContextItemSelected(item);
//		}		
//	}

	
		
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
	}
	
	public void Sair(View view){
		LancaPedido.this.finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;

	}
	
	


}
