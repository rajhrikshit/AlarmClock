package com.example.lenovo.droidalarm;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Lenovo on 17-Oct-17.
 */

public class ParentActivity extends AppCompatActivity {

    public static Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);

        toolbar=(Toolbar)findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        viewPager=(ViewPager)findViewById(R.id.pager);
        pagerAdapter=new SwipeAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        tabLayout=(TabLayout)findViewById(R.id.tab);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_action_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.item_settings){
            PopupMenu popupMenu=new PopupMenu(this,findViewById(R.id.item_settings));
            popupMenu.getMenu().add("Settings");
            popupMenu.show();

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Intent i=new Intent(ParentActivity.this,OptionsMenu.class);
                    startActivityForResult(i,999);
                    return true;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==999){
            if(resultCode==RESULT_OK){
                AlarmFragment.snooze_time=data.getExtras().getInt(AlarmFragment.SNOOZE_TIME);
                AlarmFragment.math_diff=data.getExtras().getInt(AlarmFragment.MATH_DIFF);
                AlarmFragment.shake_force=data.getExtras().getInt(AlarmFragment.SHAKE_DIFF);
            }
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBackPressed() {
        if(viewPager.getCurrentItem()==0){
            super.onBackPressed();
        }
        else
            viewPager.setCurrentItem(viewPager.getCurrentItem()-1);
    }
}
