package com.firebase.myapplication;

import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.Calendar;

import static com.firebase.myapplication.MainActivity.animBg;
import static com.firebase.myapplication.MainActivity.darkTheme;
import static com.firebase.myapplication.MainActivity.sAnimBg;
import static com.firebase.myapplication.MainActivity.sPrefTheme;

public class AboutAppActivity extends AppCompatActivity {
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadAnim();
        if (darkTheme) {
            toolbar.setBackground(getDrawable(R.color.colorPrimaryDark));
        }else {
            toolbar.setBackground(getDrawable(R.color.colorPrimary));
        }
    }
    void loadTheme() {
        sPrefTheme = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String theme = sPrefTheme.getString("list", "3");
        switch (theme){
            case "1": darkTheme = false;
                break;
            case "2": darkTheme = true;
                break;
            case "3": Calendar instance = Calendar.getInstance();
                int hour = instance.get(Calendar.HOUR_OF_DAY);
                if(hour < 7 || hour >= 20) darkTheme = true;
                else darkTheme = false;
                break;
        }
        if (darkTheme) {
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.LightTheme);
        }
    }
    void loadAnim(){
        sAnimBg = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        animBg = sAnimBg.getBoolean("switch_Anim_Bg",true);
    }
}
