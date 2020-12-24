package com.example.findshava.userView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.findshava.R;
import com.example.findshava.dataBase.Mongo.MongoPlaceLoader;
import com.example.findshava.dataBase.PlaceLoader;

public class Start extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                MongoPlaceLoader.initialize();
                assert PlaceLoader.getInstance() != null;
                Log.e("Start intent", "test");
                handler.post(transition);
            }

            private Runnable transition = () -> {
                Intent q = new Intent(Start.this, MainActivity.class);
                startActivity(q);
                finish();
                overridePendingTransition(R.transition.open_application, R.transition.close_splash);
            };
        }).start();
    }


}
