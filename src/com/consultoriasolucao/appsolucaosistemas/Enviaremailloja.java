package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Enviaremailloja extends Activity {

	public static final String EXTRA_CD_PEDIDO = "com.consultoriasolucao.appsolucaosistemas.EXTRA_CD_PEDIDO";
	private DatabaseHelper helper;
	private String cdpedido;
	private String email="";
	private SimpleDateFormat dateFormat;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		
		// prepara acesso ao banco de dados
		helper = new DatabaseHelper(this);
		
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT ds_email FROM licenca",	null);
		cursor.moveToNext();
		
		if (cursor.getCount() !=0)
		{		
		  email = cursor.getString(0);
	      cursor.close();
		}
		
		Intent intent = getIntent();
		if (intent.hasExtra(EXTRA_CD_PEDIDO)) 
		{	
			cdpedido = intent.getStringExtra(EXTRA_CD_PEDIDO);			
		} 
		
		enviaremailloja();
		
		this.finish();
	}
	
	public void enviaremailloja()
	{
		
		
		Cursor c = helper
				.getReadableDatabase()
				.rawQuery(
						"select a._id,  a.cd_prd, b.nm_prd, a.qt_iten ,a.vl_iten,(a.qt_iten*a.vl_iten),a.codbarras,d.cd_cli,d.nm_cli,c.vl_total,c.ds_obs,c.ds_formapgto,c.vl_desconto,c.dt_lancamento from itenspedido a join produto b on (a.cd_prd=b.cd_prd) join pedido c on (a.cd_pedido=c._id) join cliente d on (c.cd_cli=d.cd_cli) where a.cd_pedido= "
								+ cdpedido, null);
		
		DecimalFormat df = new DecimalFormat(",##0.00");
		StringBuilder sb = new StringBuilder();
		sb.append("<html><body><h1></h1> ");
		
		
		
		int i=0;
		while (c.moveToNext()) {

			if (i==0)//caso seja o primeiro registro o cabeçalho 
			{
				long dataChegada = c.getLong(13);
				Date dataChegadaDate = new Date(dataChegada);
				dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String periodo = dateFormat.format(dataChegadaDate);
				
				
				sb.append("PEDIDO|" + cdpedido+"|"+periodo+"|"+c.getString(12)+"|"+c.getString(7)+"|" +"Forma Pgto: " + c.getString(11)+" Obs..:"+c.getString(10) + "|");				
			}
			i =i+1;			
			sb.append("<br>");			
			sb.append("ITENSPEDIDO|"+c.getString(1)+"|"+df.format(c.getDouble(3))+"|"+df.format(c.getDouble(4))+"|"+c.getString(6)+"|");			
			
				
		}
		c.moveToFirst();
		
		sb.append(" <br/></body></html>");
		c.close();
		
		//enviar email com os itens acima
		Intent email1 = new Intent(Intent.ACTION_SEND);
		email1.putExtra(Intent.EXTRA_EMAIL, new String[]{email});		  
		email1.putExtra(Intent.EXTRA_SUBJECT, "Pedido Código: " + cdpedido);
		email1.putExtra(Intent.EXTRA_TEXT,Html.fromHtml(sb.toString()));
	//	email1.setType("message/rfc822");
		email1.setType("text/html");
		//startActivity(Intent.createChooser(email, "Choose an Email client :"));
		startActivity(email1); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}
