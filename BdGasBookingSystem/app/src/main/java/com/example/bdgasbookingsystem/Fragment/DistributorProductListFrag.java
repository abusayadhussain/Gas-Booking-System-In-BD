package com.example.bdgasbookingsystem.Fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.bdgasbookingsystem.Class.MySingleton;
import com.example.bdgasbookingsystem.Class.Product;
import com.example.bdgasbookingsystem.Class.VariableClass;
import com.example.bdgasbookingsystem.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DistributorProductListFrag extends Fragment {
    public static final String SERVER_URL= VariableClass.SERVER_URL+"getProductData.php";
    public static final String SERVER_URL_FOR_ADD_ITEM= VariableClass.SERVER_URL+"addIntoAvailableItem.php";
    public static final String SERVER_URL_FOR_DELETE_ITEM= VariableClass.SERVER_URL+"deleteFromAvailableItem.php";

    //vars
    public List<Product> productArrayList=new ArrayList<Product>();
    private ArrayAdapter<Product> productArrayAdapter;


    //widgets
    private ListView productList;

    public DistributorProductListFrag() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.distributor_product_list_frag,null);

        productList=rootView.findViewById(R.id.productList);
        populateData();

        return rootView;
    }

    private void populateData() {

        StringRequest stringRequest=new StringRequest(Request.Method.POST,SERVER_URL,new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++) {
                        try{
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            productArrayList.add(new Product(jsonObject.getString("Id"),jsonObject.getString("ProductName"),jsonObject.getString("ProductId")));
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(!productArrayList.isEmpty()){
                        populateList();
                        itemClickedCallBack();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            },new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<>();
                params.put("PhoneNumber", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                return params;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void populateList() {

        productArrayAdapter =new MyListAdapter();
        productList.setAdapter(productArrayAdapter);

    }

    private class MyListAdapter extends ArrayAdapter<Product> {

        private MyListAdapter() {
            super(getActivity(),R.layout.product_list_layout,productArrayList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.product_list_layout, parent, false);
            }

            final Product product=productArrayList.get(position);
            TextView txtView=(TextView) itemView.findViewById(R.id.productName);
            txtView.setText(product.getProductName());

            final CheckBox checkBox=(CheckBox) itemView.findViewById(R.id.checkProduct);

            if(!product.getProductId().equals("null")) {
                checkBox.setChecked(true);
            }

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final boolean isChecked = checkBox.isChecked();
                    if(isChecked) {

                        addAvailableItem(product.getId());
                    }else{
                        deleteAvailableItem(product.getId());
                    }
                }
            });

            return itemView;
        }
    }

    private void itemClickedCallBack() {

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              //  Toast.makeText(getContext(), "clicked", Toast.LENGTH_SHORT).show();
                Product product=productArrayList.get(position);
                Toast.makeText(getContext(), product.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addAvailableItem(final String productId){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, SERVER_URL_FOR_ADD_ITEM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    if(Response.equals("OK")) {
                        Toast.makeText(getContext(), "Successfully set product to available", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "The request was unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "data not saved error", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<>();
                params.put("DistPhnNum",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                params.put("ProductId",productId);
                return params;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }


    private void deleteAvailableItem(final String productId){

        StringRequest stringRequest=new StringRequest(Request.Method.POST, SERVER_URL_FOR_DELETE_ITEM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject=new JSONObject(response);
                    String Response=jsonObject.getString("response");
                    if(Response.equals("OK")) {
                        Toast.makeText(getContext(), "Successfully removed product from available list", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(), "The request was unsuccessful", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "data not saved error", Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String,String> params=new HashMap<>();
                params.put("DistPhnNum",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
                params.put("ProductId",productId);
                return params;
            }
        };

        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

}
