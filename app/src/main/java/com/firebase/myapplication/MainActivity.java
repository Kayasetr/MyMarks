package com.firebase.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity {
    public static String KEY_UPDATE_ENABLE = "is_update";
    public static final String KEY_UPDATE_VERSION = "version";
    public static String KEY_UPDATE_URL= "update_url";
    public static String KEY_TOAST= "Toast";
    private Button authButton;
    public EditText authInput, passInput;
    private TextView textReg;
    public static String name, login, password;
    private boolean tag = false;
    public static boolean darkTheme = false;
    public static boolean animBg = false;
    private CardView cardView;
    private FirebaseFirestore db;
    public static SharedPreferences sPref;
    private ProgressDialog dialog;
    private LinearLayout mainLayout;
    private ImageView background;
    public static SharedPreferences sPrefTheme, sAnimBg;
    public static RequestQueue mQueue;
    private Boolean isManager, isExist, isAdmin, autoLogin;
    public GifImageView mainBgGif;
    public static String studentName, studentCode, studentGroup, studentGroupId, wayId;
    public static int status;
    //String[] managers = {"18/3582","19/12187","16/6001"};
    private static final String admin = "18/1024";
    private Map<String, Object> user = new HashMap<>();
    List<String> managers = new ArrayList<>();
    List<String> admins = new ArrayList<>();
    public static JsonObjectRequest request;
    void getAllManagers() {
        db.collection("managers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                managers.clear();
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        managers.add(document.getId().replace("-", "/"));
                    }
                }
            }
        });
    }
    void getAllAdmins() {
        db.collection("admins").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                admins.clear();
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        admins.add(document.getId().replace("-", "/"));
                    }
                }
            }
        });
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        loadAnim();
        db = FirebaseFirestore.getInstance();
        mainLayout = findViewById(R.id.mainLayout);
        mainBgGif = findViewById(R.id.bgMain);
        getAllAdmins();
        getAllManagers();
        if (animBg){
            mainBgGif.setVisibility(View.VISIBLE);
        }else{
            mainBgGif.setVisibility(View.GONE);
        }
        if (darkTheme) {
            mainBgGif.setBackgroundResource(R.drawable.bgnight);
            mainLayout.setBackground(getDrawable(R.drawable.fade_layout_dark));
        }else {
            mainBgGif.setBackgroundResource(R.drawable.bgday);
            mainLayout.setBackground(getDrawable(R.drawable.fade_layout));
        }
        textReg = findViewById(R.id.textRegistration);
        authButton = findViewById(R.id.loginButton);
        authInput = findViewById(R.id.loginInpField);
        passInput = findViewById(R.id.passInpField);
        passInput.setVisibility(View.GONE);
        mQueue = Volley.newRequestQueue(this);
        cardView = findViewById(R.id.card_main);
        cardView.setVisibility(View.GONE);
        background = findViewById(R.id.bg);
        background.setVisibility(View.GONE);
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Подождите пожалуйста...");
        dialog.setTitle("Загрузка");
        //loadlogin();
        sPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        autoLogin = sPref.getBoolean("switch_Auto_Login",true);
        if (autoLogin){
            check();
        }
        cardView.postDelayed(new Runnable() {
            @Override
            public void run() {
                cardView.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.BounceInDown)
                        .duration(1000)
                        .repeat(0)
                        .playOn(cardView);
            }
        }, 1000);
        background.postDelayed(new Runnable() {
            @Override
            public void run() {
                background.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp)
                        .duration(1000)
                        .repeat(0)
                        .playOn(background);
            }
        }, 1000);
        textReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://rasp.homestroy.kg/online/code/";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        authButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tag){
                    login = authInput.getText().toString().trim();
                    if(login.matches("")){
                        Toast.makeText(MainActivity.this, "Введите шифр!", Toast.LENGTH_SHORT).show();
                    }else {
                        dialog.show();
                        authorization();
                    }
                   // logIn();
                }else {
                    //registration();
                }
               // login = authInput.getText().toString().trim();
               // logIn();
            }
        });
    }

    void loadAnim(){
        sAnimBg = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        animBg = sAnimBg.getBoolean("switch_Anim_Bg",true);
    }
    /*private String getAppVersion(Context context) {
        String result = "";
        try{
            result = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }*/

    public void check(){
        isManager = false;
        isAdmin = false;
        //logIn();
        //jsonParse();
        sPref = getPreferences(MODE_PRIVATE);
        login = sPref.getString("login","");
        studentName = sPref.getString("name", "");
        studentGroup = sPref.getString("group","");
        studentGroupId = sPref.getString("groupId","");
        status = sPref.getInt("status", 3);
        //getWay();
        if(!login.matches("")){
            Intent i;
            switch (status){
                case 1: isAdmin = true;
                case 2: isManager = true;
            }
                /*for (String a : admins) {
                    if(login.equals(a)){
                        isAdmin = true;
                        break;
                    }
                }
                for (String m : managers) {
                    if (login.equals(m)) {
                        isManager = true;
                        break;
                    }
                }*/
            if(isAdmin){
                i = new Intent(getBaseContext(), AdministratorActivity.class);
            }else if (isManager){
                i = new Intent(getBaseContext(), ManagerActivity.class);
            }else{
                i = new Intent(getBaseContext(), StudentActivity.class);
            }
            savelogin(login, studentName, studentGroup, studentGroupId, wayId, status);
            startActivity(i);
            finish();
        }
    }
    public static void getWay() {
        String url = "https://kayasetr.jet.kg/api/group";
        //String url = "https://rasp.homestroy.kg/api/group/all";
        request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("groups");
                            //wayId = "";
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject groups = jsonArray.getJSONObject(i);
                                String grp = groups.getString("groupId");
                                String way = groups.getString("wayId");
                                if(studentGroupId.equals(grp)){
                                    wayId = way;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //adapter2.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
    private void authorization() {
        String url = "https://kayasetr.jet.kg/api/student";
        //String url = "https://rasp.homestroy.kg/api/student/all";
        isExist = false;
        isManager = false;
        isAdmin = false;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String studentID = "";
                            JSONArray jsonArray = response.getJSONArray("students");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject student = jsonArray.getJSONObject(i);
                                studentID = student.getString("studentId");
                                studentName = student.getString("studentName");
                                studentCode = student.getString("studentLogin");
                                studentGroup = student.getString("groupName");
                                studentGroupId = student.getString("groupId");
                                //mTextViewResult.append(studentCode+"\n");
                                if (studentCode.equals(login)) {
                                    isExist = true;
                                        for (String a : admins) {
                                            if (studentCode.equals(a)) {
                                                isAdmin = true;
                                                break;
                                            }
                                        }
                                        for (String m : managers) {
                                            if (studentCode.equals(m)) {
                                                isManager = true;
                                                break;
                                            }
                                        }
                                    break;
                                }
                            }dialog.dismiss();
                            if(!isManager && isExist && isAdmin){
                                passInput.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        passInput.setVisibility(View.VISIBLE);
                                        YoYo.with(Techniques.BounceInDown)
                                                .duration(400)
                                                .repeat(0)
                                                .playOn(passInput);
                                    }
                                }, 200);
                                password = passInput.getText().toString().trim();
                                if(password.matches("")){
                                    Toast.makeText(MainActivity.this, "Введите индивуальный код!", Toast.LENGTH_SHORT).show();
                                }else {
                                    if (password.equals(studentID)){
                                        Intent i = new Intent(getBaseContext(), AdministratorActivity.class);
                                        savelogin(login, studentName, studentGroup, studentGroupId, wayId, 1);
                                        startActivity(i);
                                        finish();
                                    }else{
                                        Toast.makeText(MainActivity.this, "Вы ввели неверный шифр!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }else if(isManager && isExist && !isAdmin){
                                passInput.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        passInput.setVisibility(View.VISIBLE);
                                        YoYo.with(Techniques.BounceInDown)
                                                .duration(400)
                                                .repeat(0)
                                                .playOn(passInput);
                                    }
                                }, 200);
                                password = passInput.getText().toString().trim();
                                if(password.matches("")){
                                    Toast.makeText(MainActivity.this, "Введите индивуальный код!", Toast.LENGTH_SHORT).show();
                                }else {
                                    if (password.equals(studentID)){
                                        Intent i = new Intent(getBaseContext(), ManagerActivity.class);
                                        savelogin(login, studentName, studentGroup, studentGroupId, wayId, 2);
                                        startActivity(i);
                                        finish();
                                    }else{
                                        Toast.makeText(MainActivity.this, "Вы ввели неверный шифр!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else if(!isManager && isExist && !isAdmin){
                                Intent i = new Intent(getBaseContext(), StudentActivity.class);
                                savelogin(login, studentName, studentGroup, studentGroupId, wayId, 3);
                                startActivity(i);
                                finish();
                            }else{
                                Toast.makeText(MainActivity.this, "Вы ввели неверный шифр!", Toast.LENGTH_SHORT).show();
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
   /* void registration() {
        login = authInput.getText().toString().trim();
        //name = nameInp.getText().toString().trim();
        if (login.length() <= 3 || name.length() <= 3){
            Toast.makeText(this, "Логин и имя должны быть больше 3 символов", Toast.LENGTH_SHORT).show();
        } else if (login.substring(0, 1).matches("[0-9]")) {
            Toast.makeText(this, "Логин не может начинаться с цифры!", Toast.LENGTH_SHORT).show();
        } else if (login.contains("/") || (name.contains("/"))){
            Toast.makeText(this, "Неверные символы!", Toast.LENGTH_SHORT).show();
        }
        else if (!login.matches("") && !name.matches("")){
            user.put("login",login);
            user.put("name",name);
            db.collection("users").document(login).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.getResult().exists()){
                        Toast.makeText(MainActivity.this, "Пользователь с таким логином уже существует!", Toast.LENGTH_SHORT).show();
                    }else{
                        db.collection("users").document(login).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Успешная регистрация, войдите используя логин", Toast.LENGTH_SHORT).show();
                                MainActivity.authInput.setText(login);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Ошибка: "+e, Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            });
        }else {
            Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
        }
    }*/
    void logIn() {
        if (!login.matches("")) {
            if(!login.contains("/")){
            dialog.show();
            DocumentReference docRef = db.collection("users").document(login);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            name = document.getString("name");
                            //user.put("version",getAppVersion(MainActivity.this));
                            db.collection("users").document(login).update(user);
                            Toast.makeText(MainActivity.this, "Добро пожаловать, "+name, Toast.LENGTH_SHORT).show();
                            savelogin(login, studentName, "", "", "",1);
                            dialog.dismiss();
                            if (document.getId().substring(0, 1).matches("[0-9]")){
                                Intent i = new Intent(getBaseContext(), ManagerActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Intent i = new Intent(getBaseContext(), StudentActivity.class);
                                startActivity(i);
                                finish();
                            }
                        } else {
                            dialog.hide();
                            Toast.makeText(MainActivity.this, "Такого пользователя не существует", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        dialog.hide();
                        Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            }
            else {
                Toast.makeText(this, "Недопустимые символы!", Toast.LENGTH_SHORT).show();
            }
        } else {
            dialog.hide();
            Toast.makeText(MainActivity.this, "Введите логин!", Toast.LENGTH_SHORT).show();
            authInput.setFocusable(true);
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
    void loadlogin() {
        sPref = getPreferences(MODE_PRIVATE);
        login = sPref.getString("login","");
    }

    public static void savelogin(String login, String name, String group, String groupId, String wayId, int i) {
        SharedPreferences.Editor sLogin = sPref.edit();
        sLogin.putString("login",login);
        sLogin.putString("name",name);
        sLogin.putString("group",group);
        sLogin.putString("groupId",groupId);
        sLogin.putString("wayId",wayId);
        sLogin.putInt("status",i);
        sLogin.commit();
    }


}
