package com.firebase.myapplication;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import java.util.Calendar;

import static com.firebase.myapplication.MainActivity.KEY_UPDATE_URL;
import static com.firebase.myapplication.MainActivity.KEY_UPDATE_VERSION;
import static com.firebase.myapplication.MainActivity.animBg;
import static com.firebase.myapplication.MainActivity.darkTheme;
import static com.firebase.myapplication.MainActivity.sAnimBg;
import static com.firebase.myapplication.MainActivity.sPrefTheme;

public class UpdateCheck extends AppCompatActivity {
    private TextView tv;
    private Button btn;
    private String currentVersion, updateURL;
    private static final int PERMISSION_STORAGE_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadTheme();
        if (darkTheme) {
            setTheme(R.style.DarkTheme);
        }else {
            setTheme(R.style.LightTheme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_check);
        tv = findViewById(R.id.tvUpdate);
        btn = findViewById(R.id.updateButton);
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        currentVersion = remoteConfig.getString(KEY_UPDATE_VERSION);
        updateURL = remoteConfig.getString(KEY_UPDATE_URL);
        tv.setText("Доступно обновление\nВерсия "+currentVersion);
        if (darkTheme) {
            tv.setTextColor(getColor(R.color.colorAccent1));
        }else {
            tv.setTextColor(getColor(R.color.colorBackgroundDark));
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(MainActivity.updateURL));
                startActivity(browserIntent);*/
                if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_STORAGE_CODE);
                }else{
                    startDownloading();
                }
            }
        });

    }

    private void startDownloading() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(updateURL));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
        .setTitle("MyMarks"+currentVersion)
        .setDescription("Загрузка...")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"MyMarks.apk")
        .setMimeType("application/vnd.android.package-archive")
                .allowScanningByMediaScanner();
        DownloadManager manager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_STORAGE_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    startDownloading();
                }else{
                    Toast.makeText(this, "Отказано в доступе", Toast.LENGTH_SHORT).show();
                }
            }
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
}
