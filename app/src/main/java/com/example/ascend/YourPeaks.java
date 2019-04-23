package com.example.ascend;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class YourPeaks extends AppCompatActivity {

    private static final String TAG = "YourPeaks";
    private ArrayList<Peak> curPeaks;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent i = new Intent(YourPeaks.this, HomePage.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_peaks:
                    return true;
                case R.id.navigation_browse:
                    Intent intent = new Intent(YourPeaks.this, KIMRiseTogether.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_peaks);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_peaks);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        curPeaks = new ArrayList<Peak>();
        initCurPeaks();

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initCurPeaks();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCurPeaks();
    }


    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: called");
        RecyclerView recycler = findViewById(R.id.listOfPeaks);
        RecyclerViewAdapterPeak adapter = new RecyclerViewAdapterPeak(curPeaks, this);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initCurPeaks() {
        Log.d(TAG, "initCurPitches: called");
        curPeaks.clear();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Peak> p = realm.where(Peak.class).findAll();
        for (int i = 0; i < p.size(); i++) {
            Peak cur = p.get(i);
            curPeaks.add(cur);
        }
        initRecyclerView();
    }

    public void addPeak(View view) {
        Realm realm = Realm.getDefaultInstance();
        Calendar cal = new GregorianCalendar();
        cal.set(2019,4,10);
        Date peakStartDate = cal.getTime();
        cal.set(2019, 5, 10);
        Date peakEndDate = cal.getTime();
        Peak peak1 = new Peak("Run a Marathon", "run fast, run hard", peakStartDate, peakEndDate);
        cal.set(2019, 4, 10);
        Date phase1Start = cal.getTime();
        cal.set(2019, 4, 30);
        Date phase1End = cal.getTime();
        Phase phase1 = new Phase(phase1Start, phase1End, "Peak1Phase1", "Warming Up");
        String pitch1Start = "9:00";
        String pitch1End = "10:00";
        String pitch2Start = "11:00";
        String pitch2End = "12:00";
        Pitch pitch1 = new Pitch("4 mile run", 1, pitch1Start, pitch1End);
        Pitch pitch2 = new Pitch("Eat a Healthy Lunch", 1, pitch2Start, pitch2End);
        pitch1.plan = "Warmup";
        pitch2.plan = "2 fruits\n" + "3 veggies\n" + "1 serving protien\n" + "1 serving rice\n";
        phase1.addPitch(pitch1);
        phase1.addPitch(pitch2);
        phase1.active = true;
        peak1.addPhase(phase1);

        /*
        cal.set(2019,4,10);
        Date peak2StartDate = cal.getTime();
        cal.set(2019, 5, 10);
        Date peak2EndDate = cal.getTime();
        Peak peak2 = new Peak("Run a Marathon2", "run fast, run hard", peakStartDate, peakEndDate);
        cal.set(2019, 4, 10);
        Date phase2Start = cal.getTime();
        cal.set(2019, 4, 30);
        Date phase2End = cal.getTime();
        Phase phase12 = new Phase(phase1Start, phase1End, "Peak2Phase2", "Warming Up");
        String pitch3Start = "9:00";
        String pitch3End = "10:00";
        String pitch4Start = "11:00";
        String pitch4End = "12:00";
        Pitch pitch3 = new Pitch("4 mile run2", 1, pitch1Start, pitch1End);
        Pitch pitch4 = new Pitch("Eat a Healthy Lunch2", 1, pitch2Start, pitch2End);
        pitch3.plan = "Warmup";
        pitch4.plan = "2 fruits\n" + "3 veggies\n" + "1 serving protien\n" + "1 serving rice\n";
        phase12.addPitch(pitch3);
        phase12.addPitch(pitch4);
        phase12.active = true;
        peak2.addPhase(phase12);*/

        realm.beginTransaction();
        Peak marathon = realm.where(Peak.class).equalTo("name", "Run a Marathon").findFirst();
        if (marathon == null) realm.copyToRealm(peak1);
        /*Peak marathon2 = realm.where(Peak.class).equalTo("name", "Run a Marathon2").findFirst();
        if (marathon2 == null) realm.copyToRealm(peak2);*/
        realm.commitTransaction();
        initCurPeaks();
    }

}
