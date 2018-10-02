package com.example.bdgasbookingsystem.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bdgasbookingsystem.Activity.DistributorProductDetailsActivity;
import com.example.bdgasbookingsystem.Class.MySingleton;
import com.example.bdgasbookingsystem.Class.User;
import com.example.bdgasbookingsystem.Class.VariableClass;
import com.example.bdgasbookingsystem.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CustomerShopListFrag extends Fragment {

    public static final String SERVER_URL= VariableClass.SERVER_URL+"getShopListData.php";
    private static final String DIRECTION_URL_API = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String GOOGLE_API_KEY = "AIzaSyBi82y2avOeylyvtKFJ6UwJ7iExMznBmRw";

    //vars
    public List<User> shopArrayList=new ArrayList<User>();
    private ArrayAdapter<User> shopArrayAdapter;
    private SharedPreferences sharedPreferences;
    private ProgressDialog progressDialog;

    private String origin;


    //widgets
    private ListView shopList;

    public CustomerShopListFrag() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.customer_shop_list_frag,null);

        shopList=rootView.findViewById(R.id.shopList);
        origin=getLocationFormSharedPref();
        populateData();

        return rootView;
    }

    private void populateData() {
        progressDialog = ProgressDialog.show(getContext(), "Please wait.",
                "Finding nearest shop...!", true);
        StringRequest stringRequest=new StringRequest(Request.Method.POST,SERVER_URL,new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++) {
                        try{
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            User user=new User(jsonObject.getString("Name"),jsonObject.getString("PhoneNumber"),
                                    jsonObject.getString("Location"),jsonObject.getString("ShopName"),
                                    jsonObject.getString("UserType"),jsonObject.getString("Address"));

                            setShopList(user,jsonArray.length());

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void setShopList(final User user,final int size){

        String URL=DIRECTION_URL_API + "origin=" + origin + "&destination=" + user.getLocation() + "&key=" + GOOGLE_API_KEY;

        StringRequest stringRequest=new StringRequest(Request.Method.POST,URL,new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonData=new JSONObject(response);
                    JSONArray jsonRoutes = jsonData.getJSONArray("routes");

                    JSONObject jsonRoute = jsonRoutes.getJSONObject(0);
                    JSONArray jsonLegs = jsonRoute.getJSONArray("legs");
                    JSONObject jsonLeg = jsonLegs.getJSONObject(0);
                    JSONObject jsonDistance = jsonLeg.getJSONObject("distance");

                    shopArrayList.add(new User(user.getName(),user.getPhoneNumber(),user.getLocation(),user.getShopName(),
                            user.getUserType(),user.getAddress(),jsonDistance.getString("text"),jsonDistance.getInt("value")));

                    Log.d("json data", "onResponse: "+jsonDistance.getString("value"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(size==shopArrayList.size()){
                    populateList();
                    itemClickedCallBack();
                    progressDialog.dismiss();
                }

            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        MySingleton.getInstance(getContext()).addToRequestQueue(stringRequest);
    }

    private void populateList() {
        sortArrayList();
        shopArrayAdapter =new MyListAdapter();
        shopList.setAdapter(shopArrayAdapter);
    }

    private class MyListAdapter extends ArrayAdapter<User> {

        private MyListAdapter() {
            super(getActivity(),R.layout.shop_list_layout,shopArrayList);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View itemView = convertView;

            if (itemView == null) {
                itemView = getActivity().getLayoutInflater().inflate(R.layout.shop_list_layout, parent, false);
            }

            final User user=shopArrayList.get(position);
            TextView shopName=(TextView) itemView.findViewById(R.id.shopNameTE);
            shopName.setText(user.getShopName());

            TextView address=(TextView) itemView.findViewById(R.id.addressTE);
            address.setText(user.getAddress());

            TextView distance=(TextView) itemView.findViewById(R.id.distanceTE);
            distance.setText(user.getDistance());

            return itemView;
        }
    }

    private void sortArrayList() {
       Collections.sort(shopArrayList,User.DESCENDING_COMPARATOR);
    }

    private void itemClickedCallBack() {

        shopList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user=shopArrayList.get(position);
                Intent intent=new Intent(getContext(), DistributorProductDetailsActivity.class);
                intent.putExtra("PHONE_NUMBER",user.getPhoneNumber());
                startActivity(intent);
            }
        });
    }

    private String getLocationFormSharedPref() {

        sharedPreferences=getActivity().getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
        return sharedPreferences.getString("LOCATION_KEY","");

    }

}
