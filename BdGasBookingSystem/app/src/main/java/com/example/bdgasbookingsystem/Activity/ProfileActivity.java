package com.example.bdgasbookingsystem.Activity;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


import com.example.bdgasbookingsystem.Fragment.TabFragment;
import com.example.bdgasbookingsystem.R;

public class ProfileActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    //vars
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if(savedInstanceState==null) {
            mFragmentManager = getSupportFragmentManager();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.containerView, new TabFragment()).commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackPressed() {

        sharedPreferences=getSharedPreferences("REG",MODE_PRIVATE);
        if(sharedPreferences.getBoolean("REG_SUCC",false)){
            System.exit(0);
        }
    }

}
