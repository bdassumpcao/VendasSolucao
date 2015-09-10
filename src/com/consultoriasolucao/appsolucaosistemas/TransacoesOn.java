package com.consultoriasolucao.appsolucaosistemas;

import java.util.StringTokenizer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;

public class TransacoesOn extends Activity {

	private DatabaseHelper db;
	private String chave;
	private ProgressBar pbtransacoes;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_transacoeson);
		pbtransacoes = (ProgressBar) findViewById(R.id.pbtransacoes);
		db = new DatabaseHelper(this);

	}
	public void sockettask(View view)
	{

	}

	public void gerenciarlocalizacao(View view)
	{
		startActivity(new Intent(this, GerenciarLocalizacao.class));	
	}

	public void ConsultarEstoque(View view) {
		startActivity(new Intent(this, ConsultaProduto.class));
	}

	public void gerenciarPeidido(View view)
	{
		startActivity(new Intent(this, FiltroGerenciarPedido.class));	
	}

	public void CodigoAcesso(View view) {
		startActivity(new Intent(this, CodigodeAcesso.class));
	}

	public void ConsultarCliente(View view) {
		startActivity(new Intent(this, ConsultaCliente.class));
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void atualizarBd(View V)
	{
		if(isConected()){
			try{
				//localizando o código de acesso
				Cursor c = db.getReadableDatabase().rawQuery("select _id,  dslicenca from licenca", null);				
				c.moveToNext();
				if (c.getCount()==0)
				{
					Toast.makeText(this, "Atenção! Insira o códio de acesso antes de atualizar B.D",
							Toast.LENGTH_LONG).show();

				} else
				{		
					chave = c.getString(1);			
					c.close();
					//	Toast.makeText(this, "Atenção! Este procedimento pode levar alguns minutos!",
					//		Toast.LENGTH_LONG).show();
					@SuppressWarnings("unused")
					SQLiteDatabase banco = db.getWritableDatabase();

					new AsyncTask(){

						@Override
						protected Object doInBackground(Object... arg0) {					  


							//  selecionando os produtos
							String body = HttpRequest.get("http://www.consultoriasolucao.com/"+chave+"P.TXT").body();				
							StringTokenizer st = new StringTokenizer(body, "|");
							SQLiteDatabase banco = db.getWritableDatabase();
							//primeiro zerando o banco				
							banco.execSQL("DELETE FROM produto");
							banco.execSQL("DELETE FROM cliente");
							banco.execSQL("DELETE FROM tabelaprecoprd");
							int i=0;

							while(st.hasMoreTokens())
							{
								i=i+1;
								if (i==100)
								{i=0; }
								pbtransacoes.setProgress(i); 
								String cd_prd = st.nextToken();
								String nm_prd = st.nextToken();
								String rf_prd = st.nextToken();
								String vl_prd = st.nextToken();
								String vl_vnd = st.nextToken();
								String qt_prd = st.nextToken();
								//								String codbarras = st.nextToken();

								vl_prd =String.valueOf(vl_prd).replace(',', '.');
								vl_vnd =String.valueOf(vl_vnd).replace(',', '.');
								qt_prd =String.valueOf(qt_prd).replace(',', '.');

								ContentValues values = new ContentValues();
								values.put("cd_prd", cd_prd.trim());
								values.put("nm_prd", nm_prd.trim());
								values.put("rf_prd", rf_prd.trim());
								values.put("vl_prd", Double.valueOf(vl_prd.trim()).doubleValue());
								values.put("vl_vnd", Double.valueOf(vl_vnd.trim()).doubleValue());
								values.put("qt_prd", Double.valueOf(qt_prd.trim()).doubleValue());
								//								values.put("codbarras", codbarras.trim());
								banco.insert("produto", null, values);


							}//fil while produtos 


							//  selecionando as tabelas de preços
							String bodytp = HttpRequest.get("http://www.consultoriasolucao.com/"+chave+"TPP.TXT").body();				
							StringTokenizer sttp = new StringTokenizer(bodytp, "|");


							while(sttp.hasMoreTokens())
							{
								i=i+1;
								if (i==100)
								{i=0; }
								pbtransacoes.setProgress(i); 


								String cd_prd = sttp.nextToken();
								String cd_tabelapreco = sttp.nextToken();
								String vl_vnd = sttp.nextToken();
								String vl_percdesconto = sttp.nextToken();

								vl_percdesconto =String.valueOf(vl_percdesconto).replace(',', '.');
								vl_vnd =String.valueOf(vl_vnd).replace(',', '.');


								ContentValues values = new ContentValues();
								values.put("cd_prd", cd_prd.trim());									
								values.put("cd_tabelapreco", cd_tabelapreco.trim());
								values.put("vl_vnd", Double.valueOf(vl_vnd.trim()).doubleValue());
								values.put("vl_percdesconto", Double.valueOf(vl_percdesconto.trim()).doubleValue());
								banco.insert("tabelaprecoprd", null, values);


							}//fil while produtos

							//selecionando os clientes
							String bodycli = HttpRequest.get("http://www.consultoriasolucao.com/"+chave+"C.TXT").body();
							StringTokenizer stcli = new StringTokenizer(bodycli, "|");


							while(stcli.hasMoreTokens()){

								i=i+1;
								if (i==100)
								{i=0; }
								pbtransacoes.setProgress(i); 
								String cd_cli = stcli.nextToken();
								String nm_cli = stcli.nextToken();
								String nr_tel = stcli.nextToken();
								String nr_fax = stcli.nextToken();
								String nr_cel = stcli.nextToken();
								String nm_rua = stcli.nextToken();
								String nm_rzascl = stcli.nextToken();
								String nr_cpfcnpj = stcli.nextToken();
								String nr_rgie = stcli.nextToken();
								String nm_cpm = stcli.nextToken();
								String nr_cep = stcli.nextToken();
								String ds_obs = stcli.nextToken();
								String ds_email = stcli.nextToken();					
								String nm_brr = stcli.nextToken();
								String ds_complemento = stcli.nextToken();
								String cd_uf = stcli.nextToken();


								//inserir a linha no banco
								ContentValues values = new ContentValues();
								values.put("cd_cli", cd_cli.trim());
								values.put("nm_cli", nm_cli.trim());
								values.put("nr_tel", nr_tel.trim());
								values.put("nr_fax", nr_fax.trim());
								values.put("nr_cel", nr_cel.trim());
								values.put("nm_rua", nm_rua.trim());
								values.put("nm_rzascl", nm_rzascl.trim());
								values.put("nr_cpfcnpj", nr_cpfcnpj.trim());
								values.put("nr_rgie", nr_rgie.trim());
								values.put("nm_cpm", nm_cpm.trim());
								values.put("nr_cep", nr_cep.trim());
								values.put("ds_obs", ds_obs.trim());
								values.put("ds_email", ds_email.trim());
								values.put("nm_brr", nm_brr.trim());
								values.put("ds_complemento", ds_complemento.trim());
								values.put("cd_uf", cd_uf.trim());

								banco.insert("cliente", null, values);

							} //fim while cliente

							pbtransacoes.setProgress(100);   
							return null;
						}//fim	doInBackground

					}.execute();// AsyncTask	

				}//fim else tem código de acesso

			}catch (Exception e) {
				Toast.makeText(this, e+"", Toast.LENGTH_SHORT).show();
			}}
		else{
			Toast.makeText(this, "Sem conexão de dados", Toast.LENGTH_SHORT).show();
		}

	}//fm atuliza

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

	protected void onDestroy() {
		db.close();
		super.onDestroy();
	}

	public boolean isConected(){
		//Verifica se o dispositivo está conectado ou não
		ConnectivityManager c = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

		if(c != null
				&&((c.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) ||
						(c.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED))
				){
			return true;
		}
		return false;
	}
}
