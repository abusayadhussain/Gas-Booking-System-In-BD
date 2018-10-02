package com.example.bdgasbookingsystem.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.example.bdgasbookingsystem.Class.VariableClass;
import com.example.bdgasbookingsystem.R;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.example.bdgasbookingsystem.Class.MySingleton;


public class CustomerDetailsActivity extends AppCompatActivity {


    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String Fine_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final String SERVER_URL= VariableClass.SERVER_URL+"infoSaveCust.php";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private boolean mLocationPermissionGranted = false;
    private String phoneNum;
    private Place place;

    //widgets
    private EditText customerNameET, customerPhoneET;
    private Button chooseMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_details);

        customerNameET = findViewById(R.id.customerNameET);
        customerPhoneET = findViewById(R.id.customerPhoneET);
        chooseMap=findViewById(R.id.chooseMap);

        phoneNum=getIntent().getStringExtra("Phone");
        customerPhoneET.setText(phoneNum);
        customerPhoneET.setEnabled(false);

        getLocationPermission();

    }

    public void saveCustomer(View view) {

        if(signUp() && place!=null){

            final String name = customerNameET.getText().toString();
            final String phone = customerPhoneET.getText().toString();

            if(checkConnectivity()){

                StringRequest stringRequest=new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonObject=new JSONObject(response);
                            String Response=jsonObject.getString("response");
                            if(Response.equals("OK")) {
                                Toast.makeText(CustomerDetailsActivity.this, "data saved", Toast.LENGTH_SHORT).show();

                                sharedPreferences=getSharedPreferences("REG",MODE_PRIVATE);
                                editor=sharedPreferences.edit();
                                editor.putBoolean("REG_SUCC",true);
                                editor.apply();

                                sharedPreferences=getSharedPreferences("user_type_key",MODE_PRIVATE);
                                editor=sharedPreferences.edit();
                                editor.putString("user_type","cust");
                                editor.commit();

                                startActivity(new Intent(CustomerDetailsActivity.this,ProfileActivity.class));
                                finish();
                            }
                            else{
                                Toast.makeText(CustomerDetailsActivity.this, "data not saved", Toast.LENGTH_SHORT).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CustomerDetailsActivity.this, "data not saved error", Toast.LENGTH_SHORT).show();
                    }
                })
                {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {

                        Map<String,String> params=new HashMap<>();
                        params.put("Name",name);
                        params.put("PhoneNumber",phone);
                        params.put("Location",place.getLatLng().toString().replaceAll("[lat/lng: ()]",""));
                        params.put("UserType","cust");
                        params.put("Address",place.getAddress().toString());

                        return params;
                    }
                };

                MySingleton.getInstance(CustomerDetailsActivity.this).addToRequestQueue(stringRequest);
            }else{
                Toast.makeText(this, "Network error", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void showOnMap(View view) {

        if(mLocationPermissionGranted && checkConnectivity()){
            showPlaceSuggestions();
        }else{
            getLocationPermission();
        }

    }

    private void showPlaceSuggestions() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(CustomerDetailsActivity.this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
            //  Log.e(TAG, "onClick: GooglePlayServicesRepairableException: " + e.getMessage() );
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
            //Log.e(TAG, "onClick: GooglePlayServicesNotAvailableException: " + e.getMessage() );
        }
    }

    @SuppressLint("RestrictedApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                place = PlacePicker.getPlace(this, data);

               /* PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                        .getPlaceById(mGoogleApiClient, place.getId());*/
                chooseMap.setText(place.getName());

            }
        }
    }

    private boolean signUp() {

        boolean isValid = true;

        if (customerNameET.getText().toString().isEmpty()) {
            customerNameET.setError("Your name is mandatory");
            isValid = false;
        }

        if (customerPhoneET.getText().toString().isEmpty()) {
            customerPhoneET.setError("Phone is mandatory");
            isValid = false;
        }

        return isValid;
    }

    private void getLocationPermission() {

        String[] permissions={android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),Fine_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){

                mLocationPermissionGranted=true;
                // initMap();
            }
            else{
                ActivityCompat.requestPermissions(CustomerDetailsActivity.this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
            }

        }
        else{
            ActivityCompat.requestPermissions(CustomerDetailsActivity.this,permissions,LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        mLocationPermissionGranted=false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length>0){
                    for(int i=0;i<grantResults.length;i++) {
                        if(grantResults[i]!=PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted=false;
                            return;
                        }
                    }
                    mLocationPermissionGranted=true;

                    //initialize our map
                    // initMap();
                }
            }
        }
    }

    private boolean checkConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if(activeNetworkInfo!=null) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CustomerDetailsActivity.this,MainActivity.class));
        finish();
    }
}
