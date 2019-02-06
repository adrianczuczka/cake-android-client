package com.waracle.androidtest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.waracle.androidtest.fragments.CakeListFragment;
import com.waracle.androidtest.fragments.LoadingFragment;
import com.waracle.androidtest.pojos.Cake;
import com.waracle.androidtest.viewModels.MainActivityModel;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MainActivityModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model = ViewModelProviders.of(this).get(MainActivityModel.class);
        if (savedInstanceState == null) {
            loadCakes();
        }
    }

    public void loadCakes() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, LoadingFragment.getInstance())
                .commit();
        model.refresh();
        model.getCakes().observe(this, new Observer<ArrayList<Cake>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Cake> cakes) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, CakeListFragment.getInstance(cakes))
                        .commit();
            }
        });
    }

    public void refreshCakes(){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, LoadingFragment.getInstance())
                .commit();
        model.refresh();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            refreshCakes();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
