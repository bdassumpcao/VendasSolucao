package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class CustomAdapter extends ArrayAdapter<HashMap<String, String>>
{
	private final static String LOG = "vendas";
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
    Context context;
    int layoutResourceId;
//    public TextView cd_prd, nm_prd, vl_total;
//    public EditText qt_prd, vl_vnd;
    private LayoutInflater mInflater;
    private static ArrayList<HashMap<String, String>> produtos;

    public CustomAdapter(Context context,int layoutResourceId,ArrayList<HashMap<String, String>> list)
    {
        super(context, layoutResourceId, list);
        this.list=list;
        this.context=context;
        this.layoutResourceId=layoutResourceId;
        produtos = list;

    }
    
    public void refresh(ArrayList<HashMap<String, String>> list) {  
        produtos = list;  
        notifyDataSetChanged();  
    } 
    
    @Override
     public int getCount() {
         return list.size();
     }

    @Override
    public View getView(final int position, View convertView, ViewGroup parrent)
    {
        View view=convertView;
        final Holder holder;

        if (convertView == null)
        {
        	// Inflate the view since it does not exist
        	if(view == null){
        		mInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        		view = mInflater.inflate(layoutResourceId, null);
        	}
        	
	        holder = new Holder(view);
	        
	        view.setTag(holder);
	        
        }else{
        	holder = (Holder) view.getTag();
        }

//        hashmap_Current=new HashMap<String, String>();
//        hashmap_Current=(HashMap<String, String>) list.get(position);
        HashMap<String, String> hashmap_Current = new HashMap<String, String>();
        hashmap_Current = list.get(position);
        if(hashmap_Current != null){
        	holder.iditenpedido = hashmap_Current.get("iditenpedido");
	        holder.cd_prd.setText(hashmap_Current.get("cd_prd"));       
	        holder.nm_prd.setText(hashmap_Current.get("nm_prd"));       
	        holder.qt_prd.setText(hashmap_Current.get("qt_prd"));       
	        holder.vl_vnd.setText(hashmap_Current.get("vl_vnd"));
	        holder.vl_total.setText(hashmap_Current.get("vl_total"));

	        holder.qt_prd.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
//					if(!hasFocus)
						setProdutos(holder, v, position);				

				}
			});
	        
	        holder.vl_vnd.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
//					if(!hasFocus)
						setProdutos(holder, v, position);				

				}
			});
        }
        return view;
    }
    
    public static void setProdutos(Holder holder, View v, int position){
    	Log.i(LOG, "SETPRODUTOS");
		String qnt = holder.qt_prd.getText().toString();
		String vl_vnd = holder.vl_vnd.getText().toString();
		if(qnt.equals(""))
			qnt = "0";
		Log.i(LOG, "qnt="+qnt+" position="+position + " "+vl_vnd);

		HashMap<String, String> mapa = new HashMap<String,String>();
		mapa.put("cd_prd",  holder.cd_prd.getText().toString());
		mapa.put("iditenpedido", holder.iditenpedido);
		mapa.put("nm_prd", holder.nm_prd.getText().toString());
		mapa.put("qt_prd", qnt);
		mapa.put("vl_vnd", vl_vnd);
		if(holder.vl_total.equals(""))
			mapa.put("vl_total", "");
		else
			mapa.put("vl_total", holder.vl_total.getText().toString());
		produtos.set(position, mapa);
		Log.i(LOG, "\n SETPRD position="+ position + " cd_prd=" + holder.cd_prd.getText().toString() + " iditenpedido=" + holder.iditenpedido + " nm_prd=" + holder.nm_prd.getText().toString() + " qt_prd= " + qnt + " vl_vnd= " + vl_vnd);
    }
    
    public static ArrayList<HashMap<String, String>> getProdutos(){
    	return produtos;
    }

    private static class Holder {
    	String iditenpedido;
        public TextView cd_prd, nm_prd, vl_total;
        public EditText qt_prd, vl_vnd;
        
        public Holder(View view) {	
	        cd_prd =(TextView)view.findViewById(R.id.txt_cdprd);
	        nm_prd =(TextView)view.findViewById(R.id.txt_nmprd);        
	        qt_prd =(EditText)view.findViewById(R.id.edt_quant);
	        vl_vnd =(EditText)view.findViewById(R.id.edt_valorunt);
	        vl_total =(TextView)view.findViewById(R.id.txt_valortotal);
	        qt_prd.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
	        vl_vnd.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
    	}

    }
}
