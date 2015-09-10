package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.widget.TextView;

public class DadosProduto extends Activity {

	public static final String EXTRA_NOME_USUARIO = "com.consultoriasolucao.appsolucaosistemas.EXTRA_NOME_USUARIO";
	private DatabaseHelper helper;
	private TextView txtcodigo;
	private TextView txttabela1;
	private TextView txttabela2;
	private TextView txttabela3;
	private TextView txttabela4;
	private TextView txttabela5;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dadosproduto);
		txtcodigo = (TextView) findViewById(R.id.txtcodigo);
		TextView txtdescricao = (TextView) findViewById(R.id.txtdescricao);
		TextView txtreferencia = (TextView) findViewById(R.id.txtreferencia);
		TextView txtvalorvnd = (TextView) findViewById(R.id.txtvalorvnd);
		TextView txtquant = (TextView) findViewById(R.id.txtquant);

		//puxando os valores da tabela caso existam
		txttabela1 = (TextView) findViewById(R.id.txttabela1);
		txttabela2 = (TextView) findViewById(R.id.txttabela2);
		txttabela3 = (TextView) findViewById(R.id.txttabela3);
		txttabela4 = (TextView) findViewById(R.id.txttabela4);
		txttabela5 = (TextView) findViewById(R.id.txttabela5);

		txttabela1.setText("");
		txttabela2.setText("");
		txttabela3.setText("");
		txttabela4.setText("");
		txttabela5.setText("");




		// prepara acesso ao banco de dados
		helper = new DatabaseHelper(this);

		Intent intent = getIntent();
		if (intent.hasExtra(EXTRA_NOME_USUARIO)) {

			txtcodigo.setText(intent.getStringExtra(EXTRA_NOME_USUARIO));

			SQLiteDatabase db = helper.getReadableDatabase();
			String str ="SELECT   cd_prd,nm_prd,coalesce(rf_prd,''),coalesce(vl_vnd,0), coalesce(qt_prd,0) from produto where cd_prd='"+txtcodigo.getText().toString().trim()+"'";
			Cursor cursor = db.rawQuery(
					str, null);
			cursor.moveToFirst();
			DecimalFormat df = new DecimalFormat(",##0.00");
			txtdescricao.setText(cursor.getString(1).toString());
			txtreferencia.setText(cursor.getString(2).toString());
			txtvalorvnd.setText(df.format(cursor.getDouble(3)));
			txtquant.setText(df.format(cursor.getDouble(4))+"     ");
			cursor.close();

			//puxando as tabelas de pre�o
			Cursor cursor1 = db.rawQuery("SELECT _ID, cd_tabelapreco, vl_vnd from tabelaprecoprd where cd_prd ="+txtcodigo.getText().toString() +" order by cd_tabelapreco", null);
			int i =1;
			while (cursor1.moveToNext()) 
			{
				if (i==1) 
				{  txttabela1.setText("C�digo: " + cursor1.getString(1).trim() + "    R$: " + df.format(cursor1.getDouble(2))+"  "); }

				if (i==2) 
				{  txttabela2.setText("C�digo: " + cursor1.getString(1).trim() + "    R$: " + df.format(cursor1.getDouble(2))+"  "); }

				if (i==3) 
				{  txttabela3.setText("C�digo: " + cursor1.getString(1).trim() + "    R$: " + df.format(cursor1.getDouble(2))+"  "); }

				if (i==4) 
				{  txttabela4.setText("C�digo: " + cursor1.getString(1).trim() + "    R$: " + df.format(cursor1.getDouble(2))+"  "); }

				if (i==5) 
				{  txttabela5.setText("C�digo: " + cursor1.getString(1).trim() + "    R$: " + df.format(cursor1.getDouble(2))+"  "); }

				i =i+1;

			}
			cursor1.close();
			helper.close();


		} else {
			txtcodigo.setText("O nome do usu�rio n�o foi informado");
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}
