package com.ultimatesoftil.citron.ui.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.ultimatesoftil.citron.R;

/**
 * Created by Mike on 19/08/2018.
 */

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.splash);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);




                        ImageView img = (ImageView) findViewById(R.id.splash_img);
            TextView t1 = (TextView) findViewById(R.id.textView11);
            TextView t2 = (TextView) findViewById(R.id.textView13);
            Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash);
            Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splashup);
            img.setAnimation(animation1);
            t1.setAnimation(animation2);
            t2.setAnimation(animation2);
            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    finish();
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    i.putExtra("done",false);
                    startActivity(i);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
     }

}
