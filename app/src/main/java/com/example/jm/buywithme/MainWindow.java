package com.example.jm.buywithme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.jm.buywithme.Model.Lista;
import com.example.jm.buywithme.Model.ListAdapter;
import com.example.jm.buywithme.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainWindow extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {


    private ImageButton f_and_v, b_and_p, m_and_c, s_and_s, f_f, p_supplies, h_and_h, m_and_f, im;
    private FirebaseAuth frau;
    private Intent in;
    private GridView gv;
    private Lista list = new Lista();
    private ListAdapter listAdapter;
    private int totalHeight;
    private ArrayList<Integer> data = new ArrayList<Integer>();
    private ArrayList<Integer> data1 = new ArrayList<Integer>();
    private ArrayList<String> resultList = new ArrayList<String>();
    private Intent intent, intentForLists;
    private String section, nameList;
    private Map<String, Object> myLists = new HashMap<>();
    private Toolbar toolbar;
    private User user1;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference ref;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        user1 = new User();
        intentForLists = new Intent("eventLists");

        myLists.put("Home", list);
        myLists.put("Car", new Lista());
        nameList = "Home";
        setTitleToolBar("Home");
        /**
        for(String key: myLists.keySet()){
            //saveInTheCloud(myLists.get(key));

        }
        */

        frau = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = frau.getCurrentUser();
        ref = database.getReference();

        handler = new Handler();
        handler.postDelayed(r, 10000);

        f_and_v = (ImageButton) findViewById(R.id.f_and_v);
        b_and_p = (ImageButton) findViewById(R.id.b_and_p);
        m_and_c = (ImageButton) findViewById(R.id.m_and_c);
        m_and_f = (ImageButton) findViewById(R.id.m_and_f);
        s_and_s = (ImageButton) findViewById(R.id.s_and_s);
        f_f = (ImageButton) findViewById(R.id.f_f);
        p_supplies = (ImageButton) findViewById(R.id.p_supplies);
        h_and_h = (ImageButton) findViewById(R.id.h_and_h);
        f_and_v.setOnClickListener(this);
        b_and_p.setOnClickListener(this);
        m_and_c.setOnClickListener(this);
        s_and_s.setOnClickListener(this);
        f_f.setOnClickListener(this);
        p_supplies.setOnClickListener(this);
        h_and_h.setOnClickListener(this);
        m_and_f.setOnClickListener(this);

        gv = (GridView) findViewById(R.id.basket);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                deleteElement(i);
            }
        });

        ViewGroup.LayoutParams params = gv.getLayoutParams();
        params.height = 330;
        gv.setLayoutParams(params);
        gv.requestLayout();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("event"));

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver2, new IntentFilter("eventLists"));

        in = new Intent(MainWindow.this, SFV.class);

        intentForLists = new Intent(MainWindow.this, MyLists.class);

        //dr1.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }



    final Runnable r = new Runnable() {
        public void run() {

            //UPDATING FIREBASE
            updateFirebase();
            handler.postDelayed(this, 10000);
        }
    };

    private void updateFirebase(){
        String id = user.getUid();
        ref.child(id).child(nameList).setValue(list);

    }

    private BroadcastReceiver mMessageReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


        }
    };
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            data = intent.getIntegerArrayListExtra("data");
            Integer image = data.get(0);
            Integer imagew =  data.get(1);
            Integer id = data.get(2);
            int lock = data.get(3);
            section = in.getStringExtra("section");

            if(lock == 1){
                addElement(image, imagew, id, section);
            }else{
                deleteElement(image, imagew, id, section);

            }

        }
    };

    @Override
    protected void onDestroy(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_window, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        int id = item.getItemId();

        if (id == R.id.nav_gallery) {
            //Here we show the list
            ArrayList<String> names = new ArrayList<>();

            for(String key: myLists.keySet()){
                names.add(key);

            }

            intentForLists.putStringArrayListExtra("listNames", names);
            startActivityForResult(intentForLists, 1);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.sign_out) {
            frau.signOut();
            finish();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }



        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultcode, Intent data){

        if(requestCode == 1){
            if(data.getBooleanExtra("isNew", false)){
                int size = (data.getStringArrayListExtra("newName")).size();
                for(int i = 0; i<size; i++){
                    myLists.put(data.getStringArrayListExtra("newName").get(i), new Lista());
                }

                data.putExtra("isNew",false);
            }
            setTitleToolBar(data.getStringExtra("listName"));

            nameList = (String) data.getStringExtra("listName");
            list = (Lista) myLists.get(nameList);

            resultList = data.getStringArrayListExtra("resultList");

            refreshMyList(list.getList());
            intentForLists.putExtra("lock", false);
        }
    }

    @Override
    public void onClick(View view) {
        in.putExtra("boolean",false);

        if (view == f_and_v){
            section = "f_and_v";
            in.putExtra("section", "f_and_v");
            in.putExtra("boolean", true);
            in.putIntegerArrayListExtra("data2", list.getList("f_and_v").getListw());
            in.putIntegerArrayListExtra("data3", list.getList("f_and_v").getId());

            startActivity(in);
        }else if (view == b_and_p){
            section = "b_and_p";
            in.putExtra("boolean", true);
            in.putIntegerArrayListExtra("data2", list.getList("b_and_p").getListw());
            in.putIntegerArrayListExtra("data3", list.getList("b_and_p").getId());
            in.putExtra("section", "b_and_p");

            startActivity(in);
        }else if (view == m_and_c){
            section = "m_and_c";
            in.putExtra("boolean", true);
            in.putExtra("section", section);
            in.putIntegerArrayListExtra("data2", list.getList(section).getListw());
            in.putIntegerArrayListExtra("data3", list.getList(section).getId());

            startActivity(in);
        }else if(view == m_and_f){
            section = "m_and_f";
            in.putExtra("boolean", true);
            in.putExtra("section", section);
            in.putIntegerArrayListExtra("data2", list.getList(section).getListw());
            in.putIntegerArrayListExtra("data3", list.getList(section).getId());

            startActivity(in);
        }else if(view == s_and_s){
            section = "s_and_s";
            in.putExtra("boolean", true);
            in.putExtra("section", section);
            in.putIntegerArrayListExtra("data2", list.getList(section).getListw());
            in.putIntegerArrayListExtra("data3", list.getList(section).getId());
            startActivity(in);

        }else if(view == f_f){
            section = "f_f";
            in.putExtra("section", section);
            in.putExtra("boolean", true);
            in.putIntegerArrayListExtra("data2", list.getList(section).getListw());
            in.putIntegerArrayListExtra("data3", list.getList(section).getId());
            startActivity(in);

        }else if(view == p_supplies){
            section = "p_supplies";
            in.putExtra("section", section);
            in.putExtra("boolean", true);
            in.putIntegerArrayListExtra("data2", list.getList(section).getListw());
            in.putIntegerArrayListExtra("data3", list.getList(section).getId());

            startActivity(in);

        }else if(view == h_and_h){
            section = "h_and_h";
            in.putExtra("section", section);
            in.putExtra("boolean", true);
            in.putIntegerArrayListExtra("data2", list.getList(section).getListw());
            in.putIntegerArrayListExtra("data3", list.getList(section).getId());

            startActivity(in);
        }

    }

    private void deleteElement(int i){
        broadAddDelete(list.getList().get(i), list.getId().get(i));

        list.delete(i);

        decreaseSizeBasket();

        listAdapter = new ListAdapter(MainWindow.this, list.getList());
        gv.setAdapter(listAdapter);
    }

    private void refreshMyList(ArrayList myList){
        //In case that we need to delete from myLists
        if(resultList !=null) {
            for (String key : myLists.keySet()) {
                for(int i = 0 ; i<resultList.size() ; i++){
                    if(key.equals(resultList.get(i))){
                        myLists.remove(key);
                    }
                }
            }
        }

        listAdapter = new ListAdapter(MainWindow.this, myList);
        gv.setAdapter(listAdapter);
    }

    private void deleteElement(Integer image, Integer imagew, Integer id, String section){
        list.delete(image, imagew, id, section);

        decreaseSizeBasket();

        listAdapter = new ListAdapter(MainWindow.this, list.getList());
        gv.setAdapter(listAdapter);
    }

    private void addElement(Integer element, Integer elementw, Integer id, String section){
        list.save(element, elementw, id, section);

        broadAddDelete(elementw, id);

        increaseSizeBasket();

        listAdapter = new ListAdapter(MainWindow.this, list.getList());
        gv.setAdapter(listAdapter);
    }

    public void broadAddDelete(Integer element, Integer id){
        intent = new Intent("event1");
        data1.clear();
        data1.add(element);
        data1.add(id);
        intent.putIntegerArrayListExtra("data1", data1);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void decreaseSizeBasket(){
        if(list.size() % 3 == 0 && list.size()!=0){
            totalHeight--;
            totalHeight = list.size()/3;
        }

        ViewGroup.LayoutParams params = gv.getLayoutParams();
        params.height = 330 * totalHeight;
        gv.setLayoutParams(params);
        gv.requestLayout();
    }

    private void increaseSizeBasket(){
        totalHeight = list.size()/3;

        if(list.size() % 3 !=0){
            totalHeight++;
        }

        ViewGroup.LayoutParams params = gv.getLayoutParams();
        params.height = 330 * totalHeight;
        gv.setLayoutParams(params);
        gv.requestLayout();
    }

    private void setTitleToolBar(String name) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(name);

    }
}
