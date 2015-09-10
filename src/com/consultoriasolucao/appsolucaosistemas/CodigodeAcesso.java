package com.consultoriasolucao.appsolucaosistemas;



import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CodigodeAcesso extends Activity {
	private EditText txtdslicenca;
	private EditText txtnm_usuario;
	private EditText txtds_email;
	private DatabaseHelper helper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_codigodeacesso);
		this.txtdslicenca = (EditText) findViewById(R.id.edtcodigoacesso);	
		this.txtnm_usuario = (EditText) findViewById(R.id.txtnm_usuario);
		this.txtds_email = (EditText) findViewById(R.id.txtnm_email);

		// prepara acesso ao banco de dados
		helper = new DatabaseHelper(this);	

	}

	public void InserirCodigo (View v) {	

		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from licenca");
		ContentValues values = new ContentValues();
		values.put("dslicenca", this.txtdslicenca.getText().toString());
		values.put("ds_usuario", this.txtnm_usuario.getText().toString());
		values.put("ds_email", this.txtds_email.getText().toString());
		long resultado = db.insert("licenca", null, values);
		if(resultado != -1 )
		{
			Toast.makeText(this,"Sucesso!",  Toast.LENGTH_SHORT).show();
		}else
		{
			Toast.makeText(this, getString(R.string.erro_salvar),
					Toast.LENGTH_SHORT).show();
		}

		this.txtdslicenca.setText("");
	}

	public void VerificaCodigo(View view)
	{
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT _id, dslicenca, ds_usuario,ds_email FROM licenca",	null);
		cursor.moveToNext();

		if (cursor.getCount() !=0)
		{		
			String ds_licenca = cursor.getString(1);
			String ds_usuario = cursor.getString(2);
			String nm_email = cursor.getString(3);

			txtdslicenca.setText(ds_licenca);
			txtnm_usuario.setText(ds_usuario);
			txtds_email.setText(nm_email);
			cursor.close();
		} else Toast.makeText(this, "Não existe código de acesso cadastrado", Toast.LENGTH_LONG).show();


	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

	@Override
	protected void onDestroy() {
		helper.close();
		super.onDestroy();
	}

}
