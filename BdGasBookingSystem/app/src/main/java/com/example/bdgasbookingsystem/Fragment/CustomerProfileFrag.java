package com.example.bdgasbookingsystem.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bdgasbookingsystem.Class.MySingleton;
import com.example.bdgasbookingsystem.Class.VariableClass;
import com.example.bdgasbookingsystem.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CustomerProfileFrag extends Fragment {

    public static final String SERVER_URL= VariableClass.SERVER_URL+"getData.php";

    //vars
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    //widgets
    private TextView distributorNameTE, distributorPhoneTE,addressTE;

    public CustomerProfileFrag() {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView=inflater.inflate(R.layout.customer_profile_frag,null);

        distributorNameTE = rootView.findViewById(R.id.distributorNameTE);
        distributorPhoneTE = rootView.findViewById(R.id.distributorPhoneTE);
        addressTE = rootView.findViewById(R.id.addressTE);


        populateData();

        return rootView;
    }

    private void populateData() {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject=new JSONObject(response);
                    distributorNameTE.setText(jsonObject.getString("Name"));
                    distributorPhoneTE.setText(jsonObject.getString("PhoneNumber"));
                    addressTE.setText(jsonObject.getString("Address"));
                    saveLocationToSharedPref(jsonObject.getString("Location"));

                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Toast.makeText(DistributorDetailsActivity.this, "data not saved error", Toast.LENGTH_SHORT).show();
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

    private void saveLocationToSharedPref(String location) {

        sharedPreferences=getActivity().getSharedPreferences("LOCATION", Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putString("LOCATION_KEY",location);
        editor.apply();
    }
}
