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

import android.R.layout;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LancaPedido extends Activity implements OnItemClickListener {
	private final String LOG = "vendas";
	CheckBox cbConferir;
	TextView txtcd_pedido;
	TextView txttt_pedido;
	TextView txtcd_cli;
	TextView txtds_obs;
	TextView txtcd_tabelapreco;
	Button btsalvaPedido;
	ListView listprd;
	private EditText edt_descricao;
	private EditText edt_id;
	private EditText edt_desconto;
	private EditText edt_valorunt;
	private EditText edt_quant;
	private Spinner spnds_formapgto;
	
	Button btcd_cli;
	Integer dia, mes, ano;
	Date dt_lancamento;
	private SimpleDateFormat dateFormat;
	private DatabaseHelper helper;
	private ArrayList<Map<String, String>> produtos;
	public static final String EXTRA_CD_CLI = "com.consultoriasolucao.appsolucaosistemas.EXTRA_CD_CLI";
	public static final String EXTRA_CD_PEDIDO = "com.consultoriasolucao.appsolucaosistemas.EXTRA_CD_PEDIDO";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lancapedido);
		
        //para o teclado não aparecer automaticamente
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); 
		
		helper = new DatabaseHelper(this);
		
		this.edt_valorunt = (EditText) findViewById(R.id.edt_valorunt);
		this.edt_quant = (EditText) findViewById(R.id.edt_quant);
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
		this.btsalvaPedido = (Button) findViewById(R.id.btsalvapedido); 
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
		if(cbConferir.isChecked())
			buscaritenspedido(txtcd_pedido.getText().toString());
		else if(!cbConferir.isChecked())
			buscarprodutos();
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
//			mapa.put("iditenpedido", c.getString(0));
			mapa.put("qt_iten", df.format(c.getDouble(3)) + "   ");
			mapa.put("nm_prd", c.getString(2));
			mapa.put("vl_iten", df.format(c.getDouble(4)));
			mapa.put("vl_total", "Total R$:\n" + df.format(c.getDouble(5)) + "\n");
			produtos.add(mapa);
		}
		c.close();
		// construir objeto de retorno que é uma String[]
		return produtos;
	}
	
	
	public void buscaritenspedido(String cd_pedido) {
		String de[] = { "cd_prd", "qt_iten", "nm_prd","vl_iten", "vl_total" };
		int para[] = { R.id.txt_cdprd, R.id.edt_quant,R.id.txt_nmprd, R.id.edt_valorunt, R.id.txt_valortotal };

		SimpleAdapter adapter = new SimpleAdapter(this,
				buscaritensPedido(cd_pedido), R.layout.listview_produtospedido,
				de, para);

		listprd.setAdapter(adapter);
	}
	
	
	private ArrayList<Map<String, String>> buscarProdutos(String nome) {
		// buscar todos os produtos do banco
		if (edt_id.getText().toString().equals("")) //caso não seja consulta por código
		{
			Cursor c = helper.getReadableDatabase().rawQuery("select _id,  cd_prd, nm_prd, vl_vnd,rf_prd,qt_prd  from produto where nm_prd like '%"+nome+"%' ORDER BY nm_prd ", null);
			produtos = new ArrayList<Map<String,String>>();
			DecimalFormat df = new DecimalFormat(",##0.00");
			while (c.moveToNext()) 
			{
				Map<String, String> mapa = new HashMap<String,String>();
				mapa.put("cd_prd",  c.getString(1).trim());
				mapa.put("nm_prd", c.getString(2));
				mapa.put("qt_prd", "0"+"");
				mapa.put("vl_vnd", df.format(c.getDouble(3))+"   ");
				mapa.put("vl_total", "");
//				mapa.put("rf_prd", "Ref.: "+ c.getString(4));	
//				mapa.put("qt_prd", "Quant.: " +c.getString(5));
				produtos.add(mapa);
			}
			c.close();
			helper.close();
			// construir objeto de retorno que é uma String[]
			return produtos;
		}else
		{
			Cursor c = helper.getReadableDatabase().rawQuery("select _id,  cd_prd, nm_prd, vl_vnd,rf_prd,qt_prd  from produto where cd_prd="+edt_id.getText().toString(), null);
			produtos = new ArrayList<Map<String,String>>();
			DecimalFormat df = new DecimalFormat(",##0.00");
			while (c.moveToNext()) 
			{
				Map<String, String> mapa = new HashMap<String,String>();
				mapa.put("cd_prd",  c.getString(1).trim());
				mapa.put("nm_prd", c.getString(2));
				mapa.put("qt_prd", "0"+"");
				mapa.put("vl_vnd", df.format(c.getDouble(3))+"   ");
				mapa.put("vl_total", "");
				edt_descricao.setText(c.getString(2));
//				mapa.put("rf_prd", "Ref.: "+ c.getString(4));	
//				mapa.put("qt_prd", "Quant.: " +c.getString(5));
				produtos.add(mapa);
			}
			c.close();
			helper.close();
			// construir objeto de retorno que é uma String[]
			return produtos;
		}

	}
	
	
	public void buscarprodutos() {
		String de[] = { "cd_prd", "nm_prd", "qt_prd", "vl_vnd", "vl_total" };
		int para[] = { R.id.txt_cdprd, R.id.txt_nmprd, R.id.edt_quant, R.id.edt_valorunt, R.id.txt_valortotal };

//		SimpleAdapter adapter = new SimpleAdapter(this,
//				buscarProdutos(edt_descricao.getText().toString()), R.layout.listview_produtospedido,
//				de, para);
		CustomAdapter adapter = new CustomAdapter(this, R.layout.listview_produtospedido, buscarProdutos(edt_descricao.getText().toString()));
		listprd.setAdapter(adapter);

	}
	
	public void buscarprodutos(View view) {
		String de[] = { "cd_prd", "nm_prd", "qt_prd", "vl_vnd", "vl_total" };
		int para[] = { R.id.txt_cdprd, R.id.txt_nmprd, R.id.edt_quant, R.id.edt_valorunt, R.id.txt_valortotal };

//		SimpleAdapter adapter = new SimpleAdapter(this,
//				buscarProdutos(edt_descricao.getText().toString()), R.layout.listview_produtospedido,
//				de, para);
		CustomAdapter adapter = new CustomAdapter(this, R.layout.listview_produtospedido, buscarProdutos(edt_descricao.getText().toString()));
		listprd.setAdapter(adapter);

	}
	

	public void salvaPedido(View view)
	{

		int x = listprd.getAdapter().getCount();
		String quant = "";
		for(int i=0; i<x; i++){
			HashMap<String,String> obj = (HashMap<String,String>) listprd.getAdapter().getItem(i);
			 quant = (String) obj.get("qt_prd");
			 Log.i(LOG, "\n i="+ i + " " + quant  );
		}
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
			
				ContentValues values = new ContentValues();;
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
		DecimalFormat df = new DecimalFormat(",##0.00");
		txttt_pedido.setText(df.format(vl_total)+"");		
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
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_itenspedido, menu);
		
	}

	
		
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
	
	
	public class CustomAdapter extends ArrayAdapter<Map<String, String>>
	{
	    ArrayList<Map<String, String>> list=new ArrayList<Map<String,String>>();
	    Context context;
	    int layoutResourceId;
        public TextView cd_prd, nm_prd, vl_total;
        public EditText qt_prd, vl_vnd;

	    public CustomAdapter(Context context,int layoutResourceId,ArrayList<Map<String, String>> l)
	    {
	        super(context, layoutResourceId, l);
	        this.list=l;
	        this.context=context;
	        this.layoutResourceId=layoutResourceId;

	    }

	    @Override
	    public View getView(final int position, View convertView, ViewGroup parrent)
	    {
	        Map<String, String>hashmap_Current;
	        View view=convertView;
	        final ViewHolder holder;

	        if (view==null)
	        {
	            LayoutInflater layoutInflater=(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            view=layoutInflater.inflate(layoutResourceId, null);
	        holder=new ViewHolder();
	        cd_prd =(TextView)view.findViewById(R.id.txt_cdprd);
	        nm_prd =(TextView)view.findViewById(R.id.txt_nmprd);        
	        qt_prd =(EditText)view.findViewById(R.id.edt_quant);
	        vl_vnd =(EditText)view.findViewById(R.id.edt_valorunt);
	        vl_total =(TextView)view.findViewById(R.id.txt_valortotal);
	        view.setTag(holder);
	        }else{

	        	holder=(ViewHolder)view.getTag();
	        }

	        hashmap_Current=new HashMap<String, String>();
	        hashmap_Current=(HashMap<String, String>) list.get(position);

	        Log.e(LOG, hashmap_Current.toString());


	        cd_prd.setText(hashmap_Current.get("cd_prd"));       
	        nm_prd.setText(hashmap_Current.get("nm_prd"));       
	        qt_prd.setText(hashmap_Current.get("qt_prd"));       
	        vl_vnd.setText(hashmap_Current.get("vl_vnd"));
	        vl_total.setText(hashmap_Current.get("vl_total"));

//	        qt_prd.setOnFocusChangeListener(new OnFocusChangeListener() {
//				
//				@Override
//				public void onFocusChange(View v, boolean hasFocus) {
//					// TODO Auto-generated method stub
//					if(!hasFocus){
//						Log.i(LOG, "ONFOCUSCHANGE");
//						String s = ((EditText) v).getText().toString();
//						Log.i(LOG, s);
//						DecimalFormat df = new DecimalFormat(",##0.00");
//						Map<String, String> mapa = new HashMap<String,String>();
//						mapa.put("cd_prd",  cd_prd.getText().toString());
//						mapa.put("nm_prd", nm_prd.getText().toString());
//						mapa.put("qt_prd", s);
//						mapa.put("vl_vnd", vl_vnd.getText().toString()+"   ");
//						mapa.put("vl_total", "");
//						produtos.set(position,mapa);
//						
//					}
//				}
//			});
	        return view;
	    }

	    class ViewHolder {
	        public TextView cd_prd, nm_prd, vl_total;
	        public EditText qt_prd, vl_vnd;
	    }
	}

}
