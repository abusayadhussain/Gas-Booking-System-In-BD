package com.example.bdgasbookingsystem.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.bdgasbookingsystem.R;

import static android.content.Context.MODE_PRIVATE;


public class TabFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 2;

    //vars
    private String UserType="dist";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_layout, null);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        getUserType();

        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });


        return view;

    }

    private void getUserType() {

        sharedPreferences=getActivity().getSharedPreferences("user_type_key",MODE_PRIVATE);
        UserType=sharedPreferences.getString("user_type","");

        Log.d("bal_amar",UserType);
    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if(UserType.equals("dist")){
                        return new DistributorProfileFrag();
                    }else if(UserType.equals("cust")){
                        return new CustomerProfileFrag();
                    }
                case 1:
                    if(UserType.equals("dist")){
                        return new DistributorProductListFrag();
                    }else if(UserType.equals("cust")){
                        return new CustomerShopListFrag();
                    }

            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Profile";
                case 1:
                    if(UserType.equals("dist")){
                        return "Product List";
                    }else if(UserType.equals("cust")){
                        return "Search Product";
                    }
            }
            return null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}