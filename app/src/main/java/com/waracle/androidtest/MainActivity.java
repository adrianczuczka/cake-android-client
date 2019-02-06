package com.waracle.androidtest;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.waracle.androidtest.fragments.CakeListFragment;
import com.waracle.androidtest.fragments.LoadingFragment;
import com.waracle.androidtest.pojos.Cake;
import com.waracle.androidtest.viewModels.MainActivityModel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getName();
    private MainActivityModel model;

    /*
    ViewModel outlives rotation of an activity so it fixes the rotation problem.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        model = ViewModelProviders.of(this).get(MainActivityModel.class);
        observeCakes();
        if (savedInstanceState == null) {
            loadCakes();
            //Install HTTPResponseCache for later use.
            try {
                File httpCacheDir = new File(getApplicationContext().getCacheDir(), "http");
                long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
                HttpResponseCache.install(httpCacheDir, httpCacheSize);
            } catch (IOException e) {
                Log.e(TAG, "HTTP response cache installation failed:" + e);
            }
        }
    }

    private void loadCakes() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, LoadingFragment.getInstance())
                .commit();
        model.refresh();
    }

    private void observeCakes() {
        model.getCakes().observe(this, new Observer<ArrayList<Cake>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Cake> cakes) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, CakeListFragment.getInstance(cakes))
                        .commit();
            }
        });
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
            loadCakes();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
