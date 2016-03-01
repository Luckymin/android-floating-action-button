package com.getbase.floatingactionbutton.sample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import com.getbase.floatingactionbutton.FloatingActionButton;

public class TestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        ImageView image = (ImageView) findViewById(R.id.image);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int width = fab.getMeasuredWidth();
                int height = fab.getMeasuredHeight();
                Log.i("huang", "width : " + width + ", height : " + height);
            }
        }, 2000);

        fab.setTitle("niaho");
        fab.setIcon(R.drawable.ic_fab_star);



    }


}
