package com.firebase.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.CardView;
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
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import pl.droidsonroids.gif.GifImageView;

import static com.firebase.myapplication.MainActivity.KEY_UPDATE_ENABLE;
import static com.firebase.myapplication.MainActivity.KEY_UPDATE_URL;
import static com.firebase.myapplication.MainActivity.KEY_UPDATE_VERSION;
import static com.firebase.myapplication.MainActivity.darkTheme;
import static com.firebase.myapplication.MainActivity.animBg;
import static com.firebase.myapplication.MainActivity.sPrefTheme;
import static com.firebase.myapplication.MainActivity.sAnimBg;

public class ManagerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {
    private static final String TAG = "MainActivity";
    private Button buttonAdd,  studOutButton, showhidebtn, studRefresh;
    private EditText subjectInp, markInp, nameInp;
    private TextView textOutput;
    private String subj, mark, name, Group_;
    public static String currentVersion;
    private ListView listview;
    private CardView cardview;
    private FirebaseFirestore db;
    private Toast toast;
    private Boolean flag = true;
    //private Boolean darkTheme;
    private Boolean spinnerPress;
    //private FirebaseAuth mAuth;
    private long lastBackPressTime = 0;
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private RequestQueue mQueue;
    public GifImageView managerBgGif;
    /*private String wayId;*/
    Map<String, Object> table = new HashMap<>();
    ArrayList<String> list = new ArrayList<>();
    ArrayList<Marks> arrayList = new ArrayList<>();
    StudentAdapter adapter;
    MarksAdapter adapter1;
    ArrayAdapter<String> adapter2;
    ArrayAdapter<String> adapter3;
    List<String> data1 = new ArrayList<>();
    List<String> studArray = new ArrayList<>();
    //List<String> markList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loadAnim();
        managerBgGif = findViewById(R.id.bgManager);
        if (darkTheme) {
            managerBgGif.setBackgroundResource(R.drawable.bgnight);
            toolbar.setBackground(getDrawable(R.color.colorPrimaryDark));
        }else {
            managerBgGif.setBackgroundResource(R.drawable.bgday);
            toolbar.setBackground(getDrawable(R.color.colorPrimary));
        }
        if (animBg){
            managerBgGif.setVisibility(View.VISIBLE);
        }else{
            managerBgGif.setVisibility(View.GONE);
        }
        Group_ = MainActivity.studentGroup;
        drawer = findViewById(R.id.drawer_layout);
        cardview = findViewById(R.id.cardViewManager);
        listview = findViewById(R.id.listView);
        mQueue = Volley.newRequestQueue(this);
        //textOutput = findViewById(R.id.outputField1);
        textOutput = toolbar.findViewById(R.id.toolbarTxt);
        buttonAdd = findViewById(R.id.addButton);
        studOutButton = findViewById(R.id.studOutButton);
        studRefresh = findViewById(R.id.studRefreshButton);
        showhidebtn = findViewById(R.id.showHideButton);
        //studOut = findViewById(R.id.outStudentButton);
        nameInp = findViewById(R.id.nameInpField);
        //subjectInp = findViewById(R.id.subjInpField);
        markInp = findViewById(R.id.markInpField);
        db = FirebaseFirestore.getInstance();
        spinnerPress = false;
        adapter = new StudentAdapter(this, list);
        adapter1 = new MarksAdapter(this, arrayList);
        MainActivity.getWay();
        getSubj();
        getStudent();
        final Spinner spinner = findViewById(R.id.spinner);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data1);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                //subjectInp.setText(adapter2.getItem(position));
                if(spinnerPress){
                    subj = data1.get(position);
                }else{
                    spinner.setSelection(1);
                }
                spinnerPress = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        final Spinner spinner1 = findViewById(R.id.spinnerNameInput);
        adapter3 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, studArray);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter3);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                nameInp.setText(adapter3.getItem(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        currentVersion = remoteConfig.getString(KEY_UPDATE_VERSION);
        String appVersion = getAppVersion(this);
        if(remoteConfig.getBoolean(KEY_UPDATE_ENABLE) && !TextUtils.equals(currentVersion,appVersion)){
            Intent intnt = new Intent(getBaseContext(), UpdateCheck.class);
            startActivity(intnt);
        }
        //Показ - скрытие меню менеджера
        showhidebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) {
                    YoYo.with(Techniques.SlideOutRight)
                            .duration(500)
                            .repeat(0)
                            .playOn(cardview);
                    cardview.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            cardview.setVisibility(View.GONE);
                        }
                    }, 500);
                    showhidebtn.setText("Показать меню менеджера");
                    flag = false;
                }else {
                    YoYo.with(Techniques.SlideInLeft)
                            .duration(700)
                            .repeat(0)
                            .playOn(cardview);
                    cardview.setVisibility(View.VISIBLE);
                    showhidebtn.setText("Скрыть меню менеджера");
                    flag = true;
                }
            }
        });
        //Обнуление всех предметов студента
        studRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < data1.size(); i++) {
                    table.put(data1.get(i), 0);
                }
                name = nameInp.getText().toString().trim();
                //name = textOutput.getText().toString();
                    final AlertDialog dialog = new AlertDialog.Builder(ManagerActivity.this)
                            .setTitle(name)
                            .setMessage("Вы уверенны, что хотите обнулить все предметы студента?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection(Group_).document(name).set(table)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    //Очистка и обновление при успешном добавлении или обновлении баллов студента
                                                    arrayList.clear();
                                                    final DocumentReference docRef = db.collection(Group_).document(name);
                                                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                                            @Nullable FirebaseFirestoreException e) {
                                                            if (e != null) {
                                                                Toast.makeText(ManagerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                return;
                                                            }
                                                            if (snapshot != null && snapshot.exists()) {
                                                                Map<String, Object> map = snapshot.getData();
                                                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                                                    arrayList.add(new Marks(entry.getKey(), entry.getValue()));
                                                                }
                                                                listview.setAdapter(adapter1);
                                                            } else {
                                                                Toast.makeText(ManagerActivity.this, "Нет данных", Toast.LENGTH_SHORT).show();
                                                                textOutput.setText("Нет данных");
                                                            }
                                                        }
                                                    });
                                                    Toast.makeText(ManagerActivity.this, "Успешно добавлено в базу", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ManagerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
            }
        });
        //Вывод баллов какого-то студента, в части менеджера
       /* studOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayList.clear();
                adapter1.notifyDataSetChanged();
                name = nameInp.getText().toString().trim();
                if (!name.matches("")) {
                    final DocumentReference docRef = db.collection(Group_).document(name);
                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(ManagerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (snapshot != null && snapshot.exists()) {
                                Map<String, Object> map = snapshot.getData();
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    arrayList.add(new Marks(entry.getKey(), entry.getValue()));
                                }
                                textOutput.setText(snapshot.getId());
                                listview.setAdapter(adapter1);
                                name = snapshot.getId();
                            } else {
                                Toast.makeText(ManagerActivity.this, "Нет данных", Toast.LENGTH_SHORT).show();
                                textOutput.setText("Нет данных");
                            }
                        }
                    });
                } else {
                    Toast.makeText(ManagerActivity.this, "Напишите имя", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        //Вывод всех студентов из базы при нажатии кнопки
        studOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllStud();
            }
        });
        //Вывод всех студентов из базы при старте
        showAllStud();

        //Добавление или обновление баллов студента
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameInp.getText().toString().trim();
                /*if (!name.matches("")) {
                    final DocumentReference docRef = db.collection(Group_).document(name);
                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(ManagerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (snapshot != null && snapshot.exists()) {
                                textOutput.setText(snapshot.getId());
                            } else {
                                textOutput.setText("Нет данных");
                            }
                        }
                    });
                }*/
                mark = markInp.getText().toString().trim();
                //subj = subjectInp.getText().toString().trim();
                if (!mark.matches("") && !subj.matches("") && !name.matches("")) {
                    table.clear();
                    if(Integer.parseInt(mark) <= 100) {
                        markInp.setText("");
                        //subjectInp.setText("");
                        table.put(subj, Integer.parseInt(mark));
                        db.collection(Group_).document(name).set(table, SetOptions.merge())
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //Очистка и обновление при успешном добавлении или обновлении баллов студента
                                    arrayList.clear();
                                    final DocumentReference docRef = db.collection(Group_).document(name);
                                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                                            @Nullable FirebaseFirestoreException e) {
                                            if (e != null) {
                                                Toast.makeText(ManagerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                            if (snapshot != null && snapshot.exists()) {
                                                Map<String, Object> map = snapshot.getData();
                                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                                    arrayList.add(new Marks(entry.getKey(), entry.getValue()));
                                                }
                                                listview.setAdapter(adapter1);
                                            } else {
                                                Toast.makeText(ManagerActivity.this, "Нет данных", Toast.LENGTH_SHORT).show();
                                                textOutput.setText("Нет данных");
                                            }
                                        }
                                    });
                                    Toast.makeText(ManagerActivity.this, "Успешно добавлено в базу", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ManagerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }   else {
                        Toast.makeText(ManagerActivity.this, "Оценка не может быть больше 100 баллов!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ManagerActivity.this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Нажатие на элемент списка
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                // ListView Clicked item value
                if (list.size()>i && (listview.getAdapter() == adapter)){
                    nameInp.setText(list.get(i));
                    name = list.get(i);
                    arrayList.clear();
                    final DocumentReference docRef = db.collection(Group_).document(name);
                    docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot snapshot,
                                            @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                Toast.makeText(ManagerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (snapshot != null && snapshot.exists()) {
                                Map<String, Object> map = snapshot.getData();
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    arrayList.add(new Marks(entry.getKey(), entry.getValue()));
                                }
                                textOutput.setText(snapshot.getId());
                                listview.setAdapter(adapter1);
                                name = snapshot.getId();
                            } else {
                                Toast.makeText(ManagerActivity.this, "Нет данных", Toast.LENGTH_SHORT).show();
                                textOutput.setText("Нет данных");
                            }
                        }
                    });
                }else if (arrayList.size()>i && (listview.getAdapter() == adapter1)) {
                    name = textOutput.getText().toString();
                    db.collection(Group_).document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    List<String> list1 = new ArrayList<>();
                                    final Map<String, Object> map = document.getData();
                                    if (map != null) {
                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                                            list1.add(entry.getKey().toString());
                                        }
                                        subj = list1.get(i);
                                        spinner.setSelection(adapter2.getPosition(list1.get(i)));
                                        Toast.makeText(ManagerActivity.this, "Выбран предмет:\n"+list1.get(i), Toast.LENGTH_SHORT).show();
                                        //subjectInp.setText(list1.get(i));

                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
        //Удаление студента
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                if (list.size()>pos && (listview.getAdapter() == adapter)) {
                    final AlertDialog dialog = new AlertDialog.Builder(ManagerActivity.this)
                            .setTitle(list.get(pos))
                            .setMessage("Вы уверенны, что хотите полностью удалить сведения о студенте?")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    db.collection(Group_).document(list.get(pos)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ManagerActivity.this, "Студент успешно удален", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ManagerActivity.this, "Ошибка: "+e, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Отмена", null)
                            .show();
                    //Удаление предмета
                }else if(arrayList.size()>pos && (listview.getAdapter() == adapter1)) {
                    db.collection(Group_).document(name).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    List<String> list1 = new ArrayList<>();
                                    final Map<String, Object> map = document.getData();
                                    if (map != null) {
                                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                                            list1.add(entry.getKey().toString());
                                        }
                                        map.put(list1.get(pos), FieldValue.delete());
                                        final AlertDialog dialog = new AlertDialog.Builder(ManagerActivity.this)
                                                .setTitle("Удалить")
                                                .setMessage("Вы уверенны, что хотите полностью удалить сведения о данном предмете")
                                                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                db.collection(Group_).document(name).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        arrayList.clear();
                                                                        adapter1.notifyDataSetChanged();
                                                                        Toast.makeText(ManagerActivity.this, "Предмет успешно удален", Toast.LENGTH_SHORT).show();
                                                                        listview.setAdapter(adapter1);
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(ManagerActivity.this, "Ошибка: "+e, Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                            }
                                                }).setNegativeButton("Отмена", null)
                                                .show();
                                    }
                                }
                            }
                        }
                    });
                }
                return false;
            }
        });
        //Навигационный бар
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
        statNavOut.setText("Статус: Менеджер");
        nameNavOut.setText(MainActivity.studentName);
        groupNavOut.setText(MainActivity.studentGroup);
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
    private void getStudent() {
        String url = "https://rasp.homestroy.kg/api/student/all";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("students");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject stud = jsonArray.getJSONObject(i);
                                String studGrpName = stud.getString("groupName");
                                String studName = stud.getString("studentName");
                                if(studGrpName.equals(MainActivity.studentGroup)){
                                    studArray.add(studName);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter3.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        //getSubj();
        mQueue.add(request);
    }
    /*private void getWay() {
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
                                String grp = groups.getString("groupId");
                                String way = groups.getString("wayId");
                                if(MainActivity.studentGroupId.equals(grp)){
                                    MainActivity.wayId = way;
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
        getSubj();
        getStudent();
        mQueue.add(request);
    }*/
    private void showAllStud(){
        db.collection(Group_)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                                // spinner.setAdapter(adapter2);
                            }
                            listview.setAdapter(adapter);
                            textOutput.setText(Group_);
                        } else {
                            Toast.makeText(ManagerActivity.this, "Ошибка: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void getSubj() {
        String url = "https://rasp.homestroy.kg/api/subject/all";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //String waysId = "";
                        try {
                            JSONArray jsonArray = response.getJSONArray("subjects");
                            adapter2.clear();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject subjects = jsonArray.getJSONObject(i);
                                String subject = subjects.getString("subjectName");
                                String waysId = subjects.getString("wayId");
                                if(MainActivity.wayId.equals(waysId)){
                                    data1.add(subject);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        adapter2.notifyDataSetChanged();
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
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (listview.getAdapter() == adapter1) {
            listview.setAdapter(adapter);
            textOutput.setText(Group_);
        } else {
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
    //Нажатие на элемент навигацинного бара
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
               /* final AlertDialog dialogSetBg = new AlertDialog.Builder(this)
                        .setTitle("Фон")
                        .setMessage("Включить анимационный фон?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changeBgState(true);
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                changeBgState(false);
                            }
                        })
                        .show();*/
                break;
            case R.id.itemExit:
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Выход")
                        .setMessage("Вы уверенны, что хотите выйти?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MainActivity.savelogin("", "", "", "", "", 3);
                                Intent intent = new Intent(ManagerActivity.this, MainActivity.class);
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
