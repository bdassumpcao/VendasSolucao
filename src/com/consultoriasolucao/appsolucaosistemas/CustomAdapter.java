package com.consultoriasolucao.appsolucaosistemas;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
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
	private final String LOG = "vendas";
    ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
    Context context;
    int layoutResourceId;
//    public TextView cd_prd, nm_prd, vl_total;
//    public EditText qt_prd, vl_vnd;
    private LayoutInflater mInflater;
    private static ArrayList<HashMap<String, String>> produtos;

    public CustomAdapter(Context context,int layoutResourceId,ArrayList<HashMap<String, String>> l)
    {
        super(context, layoutResourceId, l);
        this.list=l;
        this.context=context;
        this.layoutResourceId=layoutResourceId;
        setProdutos(l);

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
        	
	        holder = new Holder();
	        holder.cd_prd =(TextView)view.findViewById(R.id.txt_cdprd);
	        holder.nm_prd =(TextView)view.findViewById(R.id.txt_nmprd);        
	        holder.qt_prd =(EditText)view.findViewById(R.id.edt_quant);
	        holder.vl_vnd =(EditText)view.findViewById(R.id.edt_valorunt);
	        holder.vl_total =(TextView)view.findViewById(R.id.txt_valortotal);
	        
	        view.setTag(holder);
	        
        }else{
        	holder = (Holder) view.getTag();
        }

//        hashmap_Current=new HashMap<String, String>();
//        hashmap_Current=(HashMap<String, String>) list.get(position);
        HashMap<String, String> hashmap_Current = new HashMap<String, String>();
        hashmap_Current = list.get(position);
        if(hashmap_Current != null){

	        holder.cd_prd.setText(hashmap_Current.get("cd_prd"));       
	        holder.nm_prd.setText(hashmap_Current.get("nm_prd"));       
	        holder.qt_prd.setText(hashmap_Current.get("qt_prd"));       
	        holder.vl_vnd.setText(hashmap_Current.get("vl_vnd").replace(".", ","));
	        holder.vl_total.setText(hashmap_Current.get("vl_total"));

	        holder.qt_prd.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					// TODO Auto-generated method stub
					if(!hasFocus){
						Log.i(LOG, "ONFOCUSCHANGE");
						String s = ((EditText) v).getText().toString();
						String vl_vnd = holder.vl_vnd.getText().toString();
						if(s.equals(""))
							s = "0";
						Log.i(LOG, "s="+s+" position="+position + " "+vl_vnd);

						HashMap<String, String> mapa = new HashMap<String,String>();
						mapa.put("cd_prd",  holder.cd_prd.getText().toString());
//						df.format(c.getDouble(3))+"   "
						mapa.put("nm_prd", holder.nm_prd.getText().toString());
						mapa.put("qt_prd", s);
						mapa.put("vl_vnd", vl_vnd);
						mapa.put("vl_total", "");
						produtos.set(position, mapa);	
						setProdutos(produtos);
					}
				}
			});
        }
        return view;
    }
    
    public void setProdutos(ArrayList<HashMap<String, String>> p){
    	this.produtos = p;   
    	for(int i=0;i<this.produtos.size();i++){
			HashMap<String,String> obj = (HashMap<String,String>) this.produtos.get(i);
			String cd_prd = (String) obj.get("cd_prd");
			String nm_prd = (String) obj.get("nm_prd");
			String quant = (String) obj.get("qt_prd");
			String vl_vnd = (String) obj.get("vl_vnd");
    		Log.i(LOG, "position="+i+" $ "+cd_prd+" $ "+nm_prd+" $ "+quant+" $ "+vl_vnd );
    	}
    }
    
    public ArrayList<HashMap<String, String>> getProdutos(){
    	return this.produtos;
    }

    private static class Holder {
        public TextView cd_prd, nm_prd, vl_total;
        public EditText qt_prd, vl_vnd;
    }
}
