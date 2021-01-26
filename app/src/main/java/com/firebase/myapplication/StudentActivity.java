package com.firebase.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import pl.droidsonroids.gif.GifImageView;

import static com.firebase.myapplication.MainActivity.KEY_UPDATE_ENABLE;
import static com.firebase.myapplication.MainActivity.KEY_UPDATE_URL;
import static com.firebase.myapplication.MainActivity.KEY_UPDATE_VERSION;
import static com.firebase.myapplication.MainActivity.darkTheme;
import static com.firebase.myapplication.MainActivity.sPrefTheme;
import static com.firebase.myapplication.MainActivity.animBg;
import static com.firebase.myapplication.MainActivity.sAnimBg;

public class StudentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Button studMarkOutButton, studOutButton;
    private EditText studName;
    private TextView studMarksOutput;
    private String nameStud, GROUP_;
    private ListView listviewMarks;
    private FirebaseFirestore db;
    private Toast toast;
    //private Boolean darkTheme, spinnerPress;
    private RequestQueue mQueue;
    private long lastBackPressTime = 0;
    private DrawerLayout drawer;
    public static String currentVersion;
    public GifImageView studentBgGif;
    //List<String> markList = new ArrayList<>();
    ArrayList<Marks> arrayList = new ArrayList<>();
    MarksAdapter adapter1;
    StudentAdapter adapter;
    ArrayList<String> list = new ArrayList<>();
    List<String> groupsList = new ArrayList<>();
    private void getGroup() {
        String url = "https://rasp.homestroy.kg/api/group/all";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("groups");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject groups = jsonArray.getJSONObject(i);
                                String grpName = groups.getString("groupName");
                                groupsList.add(grpName);
                                //adapter2.notifyDataSetChanged();
                                //mTextViewResult.append(studentCode+"\n");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadAnim();
        studentBgGif = findViewById(R.id.bgStudent);
        if (darkTheme) {
            studentBgGif.setBackgroundResource(R.drawable.bgnight);
            toolbar.setBackground(getDrawable(R.color.colorPrimaryDark));
        }else {
            studentBgGif.setBackgroundResource(R.drawable.bgday);
            toolbar.setBackground(getDrawable(R.color.colorPrimary));
        }
        if (animBg){
            studentBgGif.setVisibility(View.VISIBLE);
        }else{
            studentBgGif.setVisibility(View.GONE);
        }
        db = FirebaseFirestore.getInstance();
        GROUP_ = MainActivity.studentGroup;
       // studName = findViewById(R.id.studNameInpField);
       // studMarkOutButton = findViewById(R.id.studMarksOutButton);
        mQueue = Volley.newRequestQueue(this);
        studMarksOutput = toolbar.findViewById(R.id.toolbarTxt);
        listviewMarks = findViewById(R.id.listViewMarks);
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        currentVersion = remoteConfig.getString(KEY_UPDATE_VERSION);
        String appVersion = getAppVersion(this);
        if(remoteConfig.getBoolean(KEY_UPDATE_ENABLE) && !TextUtils.equals(currentVersion,appVersion)){
            Intent intnt = new Intent(getBaseContext(), UpdateCheck.class);
            startActivity(intnt);
        }
        /*final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);*/
        /*final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, markList);*/
        adapter = new StudentAdapter(this, list);
        adapter1 = new MarksAdapter(this, arrayList);
        db.collection(GROUP_)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            getGroup();
                            list.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                            }
                            listviewMarks.setAdapter(adapter);
                            studMarksOutput.setText(GROUP_);
                        } else {
                            Toast.makeText(StudentActivity.this, "Ошибка: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        /*studMarkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameStud = studName.getText().toString().trim();
                if (!nameStud.matches("")) {
                    final DocumentReference docRef = db.collection(GROUP_).document(nameStud);
                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(StudentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (snapshot != null && snapshot.exists()) {
                                arrayList.clear();
                                Map<String, Object> map = snapshot.getData();
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    arrayList.add(new Marks(entry.getKey(), entry.getValue()));
                                    //markList.add(entry.getKey() + ": " + entry.getValue());
                                }
                                studMarksOutput.setText(snapshot.getId());
                                listviewMarks.setAdapter(adapter1);

                            } else {
                                studMarksOutput.setText("Нет данных");
                            }
                        }
                    });
                } else {
                    Toast.makeText(StudentActivity.this, "Напишите имя", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        listviewMarks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // ListView Clicked item value
                if (list.size() > i && listviewMarks.getAdapter() != adapter1) {
                    nameStud = list.get(i);
                        final DocumentReference docRef = db.collection(GROUP_).document(nameStud);
                        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                @Nullable FirebaseFirestoreException e) {
                                if (e != null) {
                                    Toast.makeText(StudentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                if (snapshot != null && snapshot.exists()) {
                                    arrayList.clear();
                                    Map<String, Object> map = snapshot.getData();
                                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                                        arrayList.add(new Marks(entry.getKey(), entry.getValue()));
                                        //markList.add(entry.getKey() + ": " + entry.getValue());
                                    }
                                    studMarksOutput.setText(snapshot.getId());
                                    listviewMarks.setAdapter(adapter1);
                                } else {
                                    studMarksOutput.setText("Нет данных");
                                }
                            }
                        });
                    //studName.setText(list.get(i));
                }
            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        LinearLayout navigationViewLayout = headerView.findViewById(R.id.navHeader);
        ImageView grImage = headerView.findViewById(R.id.navGrImg);
        ImageView keyImage = headerView.findViewById(R.id.navKeyImg);
        TextView nameNavOut = headerView.findViewById(R.id.nameNavOut);
        TextView loginNavOut = headerView.findViewById(R.id.loginNavOut);
        TextView groupNavOut = headerView.findViewById(R.id.groupNavOut);
        TextView statNavOut = headerView.findViewById(R.id.statNavOut);
        statNavOut.setText("Статус: Студент");
        nameNavOut.setText(MainActivity.studentName);
        groupNavOut.setText(GROUP_);
        loginNavOut.setText(MainActivity.login);
        if (darkTheme) {
            DrawableCompat.setTint(grImage.getDrawable(), ContextCompat.getColor(this, R.color.colorAccent1));
            DrawableCompat.setTint(keyImage.getDrawable(),ContextCompat.getColor(this, R.color.colorAccent1));
            navigationView.setBackgroundColor(getColor(R.color.colorBackgroundDark));
            navigationViewLayout.setBackgroundColor(getColor(R.color.colorPrimaryDark));
        }else {
            DrawableCompat.setTint(grImage.getDrawable(),ContextCompat.getColor(this, R.color.colorBackgroundDark));
            DrawableCompat.setTint(keyImage.getDrawable(),ContextCompat.getColor(this, R.color.colorBackgroundDark));
            navigationView.setBackgroundColor(getColor(R.color.colorAccent1));
            navigationViewLayout.setBackgroundColor(getColor(R.color.colorPrimary));
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }


    private String getAppVersion(Context context) {
        String result = "";
        try{
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (listviewMarks.getAdapter() == adapter1) {
            listviewMarks.setAdapter(adapter);
            studMarksOutput.setText(GROUP_);
        }
        else {
            if (this.lastBackPressTime < System.currentTimeMillis() - 2000) {
                toast = Toast.makeText(this, "Нажмите снова, чтобы выйти", Toast.LENGTH_SHORT);
                toast.show();
                this.lastBackPressTime = System.currentTimeMillis();
            } else {
                if (toast != null) {
                    toast.cancel();
                    finish();
                    System.exit(0);
                }
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemOtherGr:
                Intent otherGrps = new Intent(getBaseContext(), ManagerShowOtherGroups.class);
                startActivity(otherGrps);
                break;
            case R.id.itemSettings:
                final Intent settings = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settings);
                SettingsActivity.i = getIntent();
                break;
            case R.id.itemExit:
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Выход")
                        .setMessage("Вы уверенны, что хотите выйти?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.savelogin("", "", "", "", "", 3);
                                Intent intent = new Intent(StudentActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Отмена", null)
                        .show();
                break;
            case R.id.itemAboutApp:
                final Intent aboutApp = new Intent(getBaseContext(), AboutAppActivity.class);
                startActivity(aboutApp);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
