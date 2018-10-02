package com.example.bdgasbookingsystem.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bdgasbookingsystem.Class.MySingleton;

import com.example.bdgasbookingsystem.Class.VariableClass;
import com.example.bdgasbookingsystem.R;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DistributorProductDetailsActivity extends AppCompatActivity {

    public static final String SERVER_URL = VariableClass.SERVER_URL + "getData.php";
    public static final String SERVER_URL_PRODUCT = VariableClass.SERVER_URL + "getAvailableProductForCust.php";

    //vars
    private List<String> productList = new ArrayList<>();


    //widgets
    private TextView ownerNameTE, shopNameTE, addressTE, phoneNumberTE;
    private ListView availableProductsLV;
    private ImageButton callButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distributor_product_details);

        bindData();

        String phoneNumber = getIntent().getStringExtra("PHONE_NUMBER");

        populateDistributorDetails(phoneNumber);

        populateAvailableProduct(phoneNumber);
    }

    private void bindData() {

        ownerNameTE = findViewById(R.id.ownerNameTE);
        shopNameTE = findViewById(R.id.shopNameTE);
        addressTE = findViewById(R.id.addressTE);
        phoneNumberTE = findViewById(R.id.phoneNumberTE);

        availableProductsLV = findViewById(R.id.availableProductsLV);

        callButton = findViewById(R.id.callButton);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent callIntent = new Intent(Intent.ACTION_CALL);

                callIntent.setData(Uri.parse("tel:"+phoneNumberTE.getText().toString()));
                if (ActivityCompat.checkSelfPermission(DistributorProductDetailsActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
            }
        });

    }

    private void populateDistributorDetails(final String phoneNumber) {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject=new JSONObject(response);
                    ownerNameTE.setText(jsonObject.getString("Name"));
                    phoneNumberTE.setText(jsonObject.getString("PhoneNumber"));
                    shopNameTE.setText(jsonObject.getString("ShopName"));
                    addressTE.setText(jsonObject.getString("Address"));

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
                params.put("PhoneNumber", phoneNumber);
                return params;
            }
        };


        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void populateAvailableProduct(final String phoneNumber) {

        StringRequest stringRequest=new StringRequest(Request.Method.POST,SERVER_URL_PRODUCT,new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {

                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for (int i=0;i<jsonArray.length();i++) {
                        try{
                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                            productList.add(jsonObject.getString("ProductName"));
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(!productList.isEmpty()){
                        populateList();
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
                params.put("PhoneNumber", phoneNumber);
                return params;
            }
        };

        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void populateList() {
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,android.R.id.text1,productList);
        availableProductsLV.setAdapter(adapter);
    }
}
