package com.consultoriasolucao.appsolucaosistemas;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class LancaDespesa extends Activity {

	public static final String EXTRA_CD_LANCAMENTO = "AppSolucaoSistemas.EXTRA_CD_LANCAMENTO";
	
	private EditText edtvalor;
	private EditText edthistorico;
	private DatabaseHelper db;
	private Uri outputFileUri;
	private int ano, mes, dia;
	private Button dataGasto;
	private Button dataVencimento;
	private Spinner categoria;
	private List<String> nomes = new ArrayList<String>();
	private String nome;
	private RadioGroup radioGroup;
	private RadioGroup rgreceitadespesa;
	private String datasel;
	private boolean flagvalida;
	private Date dt_lancamento, dt_vencimento;
    private String cd_lancamento;
    private SimpleDateFormat dateFormat;
    private RadioButton rgsituacaopago;
    private RadioButton rgsituacaoapagar;
    private RadioButton rgreceita;
    private RadioButton rgdespesa;
    private String straux;
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lancardespesa);
		db = new DatabaseHelper(this);

		this.edtvalor = (EditText) findViewById(R.id.edtvalor);
		this.edthistorico = (EditText) findViewById(R.id.edthistorico);
		this.radioGroup = (RadioGroup) findViewById(R.id.rgsituacao);
		this.rgreceitadespesa = (RadioGroup) findViewById(R.id.rgreceitadespesa);
        
		Calendar calendar = Calendar.getInstance();
		ano = calendar.get(Calendar.YEAR);
		mes = calendar.get(Calendar.MONTH);
		dia = calendar.get(Calendar.DAY_OF_MONTH);
		dataGasto = (Button) findViewById(R.id.btdata);
		dataGasto.setText(dia + "/" + (mes + 1) + "/" + ano);
		dataVencimento = (Button) findViewById(R.id.btdtvenciento);
		dataVencimento.setText(dia + "/" + (mes + 1) + "/" + ano);

		// busca a data atual para mostrar no botão
		SQLiteDatabase dbexe = db.getReadableDatabase();
		Cursor cursor = dbexe.rawQuery(
				"SELECT _id, ds_categoria FROM categoria order by _id", null);
		while (cursor.moveToNext()) {
			nomes.add(cursor.getString(0) + "-" + cursor.getString(1));
			
		}

		cursor.close();
		categoria = (Spinner) findViewById(R.id.categoria);

		// Cria um ArrayAdapter usando um padrão de layout da classe R do
		// android, passando o ArrayList nomes
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, nomes);
		ArrayAdapter<String> spinnerArrayAdapter = arrayAdapter;
		spinnerArrayAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_item);
		
		categoria.setAdapter(spinnerArrayAdapter);
		
		
		
		cd_lancamento=""; //codigo do lancamento e a flag para verificar se é edição ou inserção
		Intent intent = getIntent();
		if (intent.hasExtra(EXTRA_CD_LANCAMENTO)) {//caso seja edição então carregando os campos

			cd_lancamento = intent.getStringExtra(EXTRA_CD_LANCAMENTO);
			cursor = dbexe.rawQuery(
					"SELECT a._id, a.ds_historico, coalesce(a.vl_despesa,0), coalesce(a.vl_receita,0), a.dt_lancamento,a.dt_vencimento, a.cd_categoria, a.ds_situacao,  a.ds_tipo,b.ds_categoria from financas a join categoria b on (a.cd_categoria=b._id) where a._id="+cd_lancamento, null);
			while (cursor.moveToNext()) {
				edthistorico.setText(cursor.getString(1));				
				Double val =Double.valueOf(cursor.getString(2).toString()).doubleValue()+Double.valueOf(cursor.getString(3).toString()).doubleValue();
				edtvalor.setText(val+"");
				long dataChegada = cursor.getLong(4);
				Date dataChegadaDate = new Date(dataChegada);
				dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				String periodo = dateFormat.format(dataChegadaDate);
				dataGasto.setText(periodo);
				
				dataChegada = cursor.getLong(5);
				periodo = dateFormat.format(dataChegadaDate);
				dataVencimento.setText(periodo);
				
				rgsituacaopago = (RadioButton) findViewById(R.id.rgsituacaopgo);
				rgsituacaoapagar = (RadioButton) findViewById(R.id.rgsituacaoavencer);
				if (cursor.getString(7).equals("P")) //caso a situação seja pago
				{
					rgsituacaopago.setChecked(true);	
					rgsituacaoapagar.setChecked(false);
				} else 
				{
					rgsituacaopago.setChecked(false);	
					rgsituacaoapagar.setChecked(true);
					
				}
				
				
				rgreceita = (RadioButton) findViewById(R.id.rgreceita);
				rgdespesa = (RadioButton) findViewById(R.id.rgdespesa);
				if (cursor.getString(8).equals("D")) //caso a situação seja pago
				{
					rgreceita.setChecked(false);	
					rgdespesa.setChecked(true);
				} else 
				{
					rgreceita.setChecked(true);	
					rgdespesa.setChecked(false);
					
				}
				
				//verificando qual item do sppiner foi selecinado	
				straux = cursor.getString(6)+"-"+cursor.getString(9);							 	
				int spinnerPosition = arrayAdapter.getPosition(straux);
				categoria.setSelection(spinnerPosition);			 
				 
			}

			cursor.close();

		}

	}
	

	

	public void selecionarDataLancamento(View view) {
		showDialog(view.getId());
		datasel = "DataLancamento";

	}

	public void selecionarDataVencimento(View view) {
		showDialog(view.getId());
		datasel = "DataVencimento";
	}

	private OnDateSetListener listener = new OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			ano = year;
			mes = monthOfYear;
			dia = dayOfMonth;

			if (datasel == "DataLancamento") {
				dataGasto.setText(dia + "/" + (mes + 1) + "/" + ano);
			} else
				dataVencimento.setText(dia + "/" + (mes + 1) + "/" + ano);
		}
	};

	@Override
	protected Dialog onCreateDialog(int id) {
		if (R.id.btdata == id) {
			return new DatePickerDialog(this, listener, ano, mes, dia);
		}

		if (R.id.btdtvenciento == id) {
			return new DatePickerDialog(this, listener, ano, mes, dia);
		}
		return null;
	}

	public void InserirDespesa(View view) {
		flagvalida = true;

		if (edtvalor.getText().toString().equals("")) {
			Toast.makeText(this, "Entre com o valor da despesa",
					Toast.LENGTH_LONG).show();
			flagvalida = false;

		}

		if (edthistorico.getText().toString().equals("") ) {
			Toast.makeText(this, "Entre com a descrição da despesa",
					Toast.LENGTH_LONG).show();
			flagvalida = false;

		}

		if (flagvalida) {
			
			SQLiteDatabase banco = db.getWritableDatabase();
			
			if (cd_lancamento != ""){ //caso seja edição então apagando o registro e inserindo outro
				
				banco.execSQL("delete from financas where _id="+cd_lancamento);	
			}
			
			ContentValues values = new ContentValues();
			values.put("ds_historico", edthistorico.getText().toString());
			
			
			
			dt_lancamento = ConvertToDate(dataGasto.getText().toString());
			values.put("dt_lancamento", dt_lancamento.getTime());
			dt_vencimento = ConvertToDate(dataVencimento.getText().toString()); 
			values.put("dt_vencimento", dt_vencimento.getTime());

			
			
			int tipo = radioGroup.getCheckedRadioButtonId();
			if (tipo == R.id.rgsituacaopgo) {
				values.put("ds_situacao", "P");
			} else
				values.put("ds_situacao", "A");

			tipo = rgreceitadespesa.getCheckedRadioButtonId();
			if (tipo == R.id.rgdespesa) 
			{
				values.put("ds_tipo", "D");
				values.put("vl_despesa",Double.valueOf(edtvalor.getText().toString()).doubleValue());
				values.put("vl_receita", 0);
			} else 
			{  
				values.put("ds_tipo", "R");
				values.put("vl_receita",Double.valueOf(edtvalor.getText().toString()).doubleValue());
				values.put("vl_despesa", 0);
			}
			
			String exemplo = categoria.getSelectedItem().toString();
			int posicao = exemplo.indexOf("-");
			exemplo = exemplo.substring(0, posicao);
			values.put("cd_categoria", exemplo);

			long resultado = banco.insert("financas", null, values);
			edthistorico.setText("");
			edtvalor.setText("");
			Toast.makeText(this, "Lançamento salvo com sucesso!",
					Toast.LENGTH_LONG).show();
		}
	}
	
	private Date ConvertToDate(String dateString)
	{
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

	}

	public void tiraFoto(View view) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(Environment.getExternalStorageDirectory(),
				"test.jpg");
		outputFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, 9000);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.solucao_sistemas, menu);
		return true;
	}

}