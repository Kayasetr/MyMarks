package com.firebase.myapplication;

import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import pl.droidsonroids.gif.GifImageView;

import static com.firebase.myapplication.MainActivity.animBg;
import static com.firebase.myapplication.MainActivity.darkTheme;
import static com.firebase.myapplication.MainActivity.sAnimBg;
import static com.firebase.myapplication.MainActivity.sPrefTheme;

public class ManagerShowOtherGroups extends AppCompatActivity {
    private Boolean darkTheme, isSpinnerPressed;
    private Spinner spinnerSt;
    ArrayAdapter<String> adapter2;
    private RequestQueue mQueue;
    private String nameStud, GROUP_;
    private ListView listviewMarks;
    private FirebaseFirestore db;
    private TextView studOut;
    private SlidrInterface slidr;
    public GifImageView otherGrGifBg;
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
    protected void onCreate(Bundle savedInstanceState) {
        loadTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_show_other_groups);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadAnim();
        otherGrGifBg = findViewById(R.id.bgOtherGroups);
        if (darkTheme && animBg) {
            otherGrGifBg.setBackgroundResource(R.drawable.bgnight);
            toolbar.setBackground(getDrawable(R.color.colorPrimaryDark));
        }else if (!darkTheme && animBg){
            otherGrGifBg.setBackgroundResource(R.drawable.bgday);
            toolbar.setBackground(getDrawable(R.color.colorPrimary));
        }else if (darkTheme && !animBg){
            otherGrGifBg.setBackgroundResource(R.color.colorBackgroundDark);
            toolbar.setBackground(getDrawable(R.color.colorPrimaryDark));
        }else{
            otherGrGifBg.setBackgroundResource(R.color.colorAccent1);
            toolbar.setBackground(getDrawable(R.color.colorPrimary));
        }
        db = FirebaseFirestore.getInstance();
        mQueue = Volley.newRequestQueue(this);
        GROUP_ = MainActivity.studentGroup;
        studOut = toolbar.findViewById(R.id.toolbarTxt);
        slidr = Slidr.attach(this);
        slidr.unlock();
        isSpinnerPressed = false;
        getGroup();
        listviewMarks = findViewById(R.id.listViewMarks);
        adapter = new StudentAdapter(this, list);
        adapter1 = new MarksAdapter(this, arrayList);
        spinnerSt = findViewById(R.id.spinnerGroups);
        adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, groupsList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSt.setAdapter(adapter2);
        spinnerSt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                //subjectInp.setText(adapter2.getItem(position));
                if(isSpinnerPressed){
                    GROUP_ = groupsList.get(position);
                    db.collection(GROUP_)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        list.clear();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            list.add(document.getId());
                                        }
                                        studOut.setText(GROUP_);
                                        listviewMarks.setAdapter(adapter);
                                    } else {
                                        Toast.makeText(ManagerShowOtherGroups.this, "Ошибка: " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }else{
                    spinnerSt.setSelection(adapter2.getPosition(MainActivity.studentGroup));
                }
                isSpinnerPressed = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        db.collection(GROUP_)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                list.add(document.getId());
                            }
                            studOut.setText(GROUP_);
                            listviewMarks.setAdapter(adapter);
                        } else {
                            Toast.makeText(ManagerShowOtherGroups.this, "Ошибка: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                                Toast.makeText(ManagerShowOtherGroups.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            if (snapshot != null && snapshot.exists()) {
                                arrayList.clear();
                                Map<String, Object> map = snapshot.getData();
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    arrayList.add(new Marks(entry.getKey(), entry.getValue()));
                                    //markList.add(entry.getKey() + ": " + entry.getValue());
                                }
                                studOut.setText(snapshot.getId());
                                listviewMarks.setAdapter(adapter1);
                            } else {
                                studOut.setText("Нет данных");
                            }
                        }
                    });
                    //studName.setText(list.get(i));
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    public void onBackPressed() {
       if (listviewMarks.getAdapter() == adapter1) {
            listviewMarks.setAdapter(adapter);
            studOut.setText(GROUP_);
        }else{
           finish();
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
