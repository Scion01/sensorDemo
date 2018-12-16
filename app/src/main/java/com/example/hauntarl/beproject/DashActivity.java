package com.example.hauntarl.beproject;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class DashActivity extends AppCompatActivity implements Extract.OnFragmentInteractionListener, Map.OnFragmentInteractionListener, Upload.OnFragmentInteractionListener, Driver.OnFragmentInteractionListener {

    private static   ViewPager viewPager;
    private static  PagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_extract));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_trail));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_cloud_upload_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_public_black_24dp));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        adapter = new PagerAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                TextView heading = (TextView)findViewById(R.id.header);
                int position=tab.getPosition();
                if(position == 0)
                    heading.setText(R.string.extraction);
                if(position == 1)
                    heading.setText(R.string.map);
                if(position == 2)
                    heading.setText(R.string.upload);
                if(position == 3)
                    heading.setText(R.string.di);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
    public static void switchFragments(){
        viewPager.setCurrentItem(0);
    }
}
