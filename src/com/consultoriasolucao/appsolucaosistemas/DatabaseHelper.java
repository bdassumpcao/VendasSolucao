package com.consultoriasolucao.appsolucaosistemas;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String BANCO_DADOS = "SOLUCAO";
	private static int VERSAO = 18;
	
	public DatabaseHelper(Context context) 
	{
	  super(context, BANCO_DADOS, null, VERSAO);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) 
	{
		
		
		
		db.execSQL("CREATE TABLE licenca (_id INTEGER PRIMARY KEY, dslicenca TEXT);");
		
		db.execSQL("CREATE TABLE produto (_id INTEGER PRIMARY KEY, cd_prd INTEGER, nm_prd TEXT, rf_prd TEXT,vl_prd DOUBLE, vl_vnd DOUBLE, qt_prd DOUBLE);");
		
		db.execSQL("CREATE TABLE cliente (_id INTEGER PRIMARY KEY, cd_cli INTEGER, nm_cli TEXT, nr_tel TEXT, nr_fax TEXT, nr_cel TEXT, nm_rua TEXT, nm_rzascl TEXT, nr_cpfcnpj TEXT, nr_rgie TEXT, nm_cpm TEXT, nr_cep TEXT, ds_obs TEXT, ds_email TEXT, nm_brr TEXT, ds_complemento TEXT);");
		
	    db.execSQL("ALTER TABLE cliente ADD cd_uf TEXT"); 
	    db.execSQL("CREATE TABLE coordenadagps (_id INTEGER, dt_lancamento DATE, hr_lancamento DOUBLE,  ds_usuario TEXT); ");
		db.execSQL("ALTER TABLE licenca ADD ds_usuario TEXT"); 
		db.execSQL("ALTER TABLE licenca ADD ds_email TEXT"); 
		db.execSQL("ALTER TABLE coordenadagps ADD vl_latitudec TEXT");
		db.execSQL("ALTER TABLE coordenadagps ADD  vl_longitudec TEXT");
		
//		if (oldVersion <7) 
			//{
				 db.execSQL("CREATE TABLE pedido (_id INTEGER, dt_lancamento DATE, vl_bruto DOUBLE, vl_desconto DOUBLE, vl_total DOUBLE,cd_cli integer); ");
			      db.execSQL("CREATE TABLE itenspedido (_id INTEGER, cd_pedido INTEGER, cd_prd INTEGER, qt_iten DOUBLE, vl_iten DOUBLE); ");
		//	}
			
			//if (oldVersion <13) 
		//	{
				 db.execSQL("CREATE TABLE tabelaprecoprd (_id INTEGER, vl_vnd DOUBLE, vl_percdesconto DOUBLE, cd_prd integer, cd_tabelapreco integer); ");
			     
			//}	   
			
		//	if (oldVersion <14) 
			//{
			  db.execSQL("DROP TABLE pedido;");
			  db.execSQL("DROP TABLE itenspedido;");
		  	  db.execSQL("CREATE TABLE pedido (_id INTEGER PRIMARY KEY, dt_lancamento DATE, vl_bruto DOUBLE, vl_desconto DOUBLE, vl_total DOUBLE,cd_cli integer, DS_OBS TEXT); ");
		      db.execSQL("CREATE TABLE itenspedido (_id INTEGER PRIMARY KEY, cd_pedido INTEGER, cd_prd INTEGER, qt_iten DOUBLE, vl_iten DOUBLE); ");
			//}
		    
		      //if (oldVersion <16)
			//	{
				 db.execSQL("ALTER TABLE pedido ADD  ds_formapgto TEXT");
				//}
				 
			//	 if (oldVersion <17)
				//	{
						db.execSQL("ALTER TABLE pedido ADD  cd_tabelapreco integer");
					//}
				 
				
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		
	    
		 if (oldVersion <17)
		{
			db.execSQL("ALTER TABLE pedido ADD  cd_tabelapreco integer");
		}
	    
		
	}
	
}
