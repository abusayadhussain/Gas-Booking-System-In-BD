package com.example.bdgasbookingsystem.Class;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by User on 1/10/2018.
 */

public class MySingleton {

    private static MySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context context;

    private MySingleton(Context context){
        this.context=context;
        requestQueue=getRequestQueue();
    }

    private RequestQueue getRequestQueue(){
        if(requestQueue==null){
            requestQueue= Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static synchronized MySingleton getInstance(Context context){
        if(mInstance==null){
            mInstance=new MySingleton(context);
        }
        return mInstance;
    }

    public<T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
