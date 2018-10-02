package com.example.bdgasbookingsystem.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.bdgasbookingsystem.Class.VariableClass;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.example.bdgasbookingsystem.Class.MySingleton;
import com.example.bdgasbookingsystem.R;

public class MainActivity extends AppCompatActivity {

    private  final int REQUEST_LOGIN = 123;
    private ProgressDialog progressDialog;
    private static Handler handler = new Handler(Looper.getMainLooper());

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final String SERVER_URL= VariableClass.SERVER_URL+"getData.php";


    //vars
    private String userType;
    private String UserType;

    //widgets
    private Button customerBtn,distributorBtn;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
        if(checkConnectivity()){

            relativeLayout.setVisibility(View.VISIBLE);
        }else{
            relativeLayout.setVisibility(View.GONE);
            showNetworkError();
        }

        sharedPreferences=getSharedPreferences("REG",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("REG_SUCC",false)){
            handleFirebaseAuth();
        }


    }

    private void init() {

        customerBtn=(Button) findViewById(R.id.customerBtn);
        distributorBtn=(Button) findViewById(R.id.distributorBtn);
        relativeLayout=findViewById(R.id.container);

        customerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType="customer";
                handleFirebaseAuth();
            }
        });

        distributorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userType="distributor";
               handleFirebaseAuth();
            }
        });
    }

    private void getUserType() {

        StringRequest stringRequest=new StringRequest(Request.Method.POST, SERVER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject jsonObject=new JSONObject(response);
                    UserType=jsonObject.getString("UserType");
                    if(!UserType.equals("null")) {
                        if(UserType.equals("dist")){
                            saveUserTypeToSharedPref(UserType);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                    finish();
                                }
                            },3000);

                        }else if(UserType.equals("cust")){
                            saveUserTypeToSharedPref(UserType);

                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressDialog.dismiss();
                                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                                    finish();
                                }
                            },3000);
                        }
                    }else {
                        if(userType!=null){
                        if(userType.equals("customer")){
                            startActivity(new Intent(MainActivity.this, CustomerDetailsActivity.class)
                                    .putExtra("Phone", FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
                            finish();

                        }else if(userType.equals("distributor")){
                            startActivity(new Intent(MainActivity.this, DistributorDetailsActivity.class)
                                    .putExtra("Phone",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()));
                            finish();
                        }
                        }else{
                            progressDialog.dismiss();
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                    }

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
                params.put("PhoneNumber",FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                return params;
            }
        };

        MySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);


    }

    private void saveUserTypeToSharedPref(String userType){
        sharedPreferences=getSharedPreferences("user_type_key",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putString("user_type",userType);
        editor.apply();

        sharedPreferences=getSharedPreferences("REG",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putBoolean("REG_SUCC",true);
        editor.apply();
    }

    private void handleFirebaseAuth() {
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        if (checkConnectivity()) {
            if (auth.getCurrentUser() != null) {
                if (!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()) {

                    progressDialog = ProgressDialog.show(this, "Please wait.",
                            "logging in..!", true);
                    relativeLayout.setVisibility(View.GONE);
                    getUserType();
                }
            } else {
                    startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(
                                        Arrays.asList(
                                                new AuthUI.IdpConfig.PhoneBuilder().build()))
                                                .build(), REQUEST_LOGIN);
            }
        }
        else {
            relativeLayout.setVisibility(View.GONE);
            showNetworkError();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_LOGIN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if(resultCode == RESULT_OK){
                if(!FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().isEmpty()){
                    progressDialog = ProgressDialog.show(this, "Please wait.",
                            "logging in..!", true);
                    relativeLayout.setVisibility(View.GONE);
                    getUserType();
                   // finish();
                    return;
                }
                else{
                    if(response == null) {
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast.makeText(this, "No Net Connection", Toast.LENGTH_SHORT).show();

                        return;
                    }
                    if(response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast.makeText(this, "Unknown Error Occours", Toast.LENGTH_SHORT).show();

                        return;
                    }
                }
                Toast.makeText(this,"Unknown Sign In Occours",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showNetworkError() {
        Dialog dialog = new Dialog(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("You are offline please check your internet connection and retry")
                .setTitle("No Internet Connection")
                .setPositiveButton("Refresh", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(new Intent(MainActivity.this,MainActivity.class));
                    }
                });

        dialog = builder.create();
        dialog.show();
        dialog.setCancelable(false);
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
}
