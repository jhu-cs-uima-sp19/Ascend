package com.example.ascend;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;

public class peak_phases extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView mTextMessage;

    private static final String TAG = "peak_phases";
    private ArrayList<Phase> curPhases;
    private Button changeStart;
    private Button changeEnd;
    private String peakname;
    private TextView startDate;
    private TextView endDate;
    private TextView Name;
    private boolean fromStart;
    private Realm realm;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent i = new Intent(peak_phases.this, HomePage.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_peaks:
                    return true;
                case R.id.navigation_browse:
                    Intent intent = new Intent(peak_phases.this, BrowseFirstPage.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peak_phases);
        Intent i = getIntent();
        peakname = i.getStringExtra("peakname");
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_peaks);
        navigation.setItemIconTintList(null);

        changeStart = findViewById(R.id.change_start);

        changeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                fromStart = true;
            }
        });

        changeEnd = findViewById(R.id.change_end);

        changeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                fromStart = false;
            }
        });

        Realm.init(this);
        realm = Realm.getDefaultInstance();

        TextView startDate = findViewById(R.id.startDate);
        TextView endDate = findViewById(R.id.endDate);

        TextView Name = findViewById(R.id.peakName);
        Name.setText(peakname);
        Peak peak = realm.where(Peak.class).equalTo("name", peakname).findFirst();
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(peak.start);
        startDate.setText(currentDateString);
        currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(peak.end);
        endDate.setText(currentDateString);

        curPhases = new ArrayList<Phase>();
        initCurPeaks();
    }

    public void back(View v) {
        Intent i = new Intent(peak_phases.this, YourPeaks.class);
        startActivity(i);
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: called");
        RecyclerView recycler = findViewById(R.id.listOfPeaks);
        RecyclerViewAdapterPhase adapter = new RecyclerViewAdapterPhase(curPhases, this, peakname);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initCurPeaks() {
        Log.d(TAG, "initCurPitches: called");
        Realm realm = Realm.getDefaultInstance();
        Peak peaky = realm.where(Peak.class).equalTo("name", peakname).findFirst();
        for (int i = 0; i < peaky.phase.size(); i++) {
            Phase cur = peaky.phase.get(i);
            curPhases.add(cur);

        }
        if (curPhases.size() > 0 ) {
            initRecyclerView();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        Log.d(TAG, "onDateSet: " + view.getId());

        if (fromStart) {
            Log.d(TAG, "onDateSet: in here");
            startDate = findViewById(R.id.startDate);
            startDate.setText(currentDateString);
            Date d = c.getTime();
            Peak curPeak = realm.where(Peak.class).equalTo("name", peakname).findFirst();
            realm.beginTransaction();
            curPeak.start = d;
            realm.commitTransaction();
            Snackbar.make(changeStart, "Date changed!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        else {
            Log.d(TAG, "onDateSet: in there");
            endDate = findViewById(R.id.endDate);
            endDate.setText(currentDateString);
            Date d = c.getTime();
            Peak curPeak = realm.where(Peak.class).equalTo("name", peakname).findFirst();
            realm.beginTransaction();
            curPeak.end = d;
            realm.commitTransaction();
            Snackbar.make(changeEnd, "Date changed!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

    }

    public void onClick(View v) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Peak peaky = realm.where(Peak.class).equalTo("name", peakname).findFirst();
        int len = peaky.phase.size();
        Log.d(TAG, "onClick: " + len);
        for (int i = 0; i < len; i++) {
            Phase cur = peaky.phase.get(i);
            RealmList<Pitch> phasey = cur.all;
            Log.d(TAG, "onClick: " + cur.name);
            for (int j = 0; j < phasey.size(); j++) {
                Pitch cur2 = phasey.get(j);
                cur2.deleteFromRealm();
            }
            RealmList<Pitch> Sunday = cur.sunday;
            for (int j = 0; j < Sunday.size(); j++) {
                Pitch cur2 = Sunday.get(j);
                cur2.deleteFromRealm();
            }
            RealmList<Pitch> Monday = cur.monday;
            for (int j = 0; j < Monday.size(); j++) {
                Pitch cur2 = Monday.get(j);
                cur2.deleteFromRealm();
            }
            RealmList<Pitch> tuesday = cur.tuesday;
            for (int j = 0; j < tuesday.size(); j++) {
                Pitch cur2 = tuesday.get(j);
                cur2.deleteFromRealm();
            }
            RealmList<Pitch> wednesday = cur.wednesday;
            for (int j = 0; j < wednesday.size(); j++) {
                Pitch cur2 = wednesday.get(j);
                cur2.deleteFromRealm();
            }
            RealmList<Pitch> thur = cur.thursday;
            for (int j = 0; j < thur.size(); j++) {
                Pitch cur2 = thur.get(j);
                cur2.deleteFromRealm();
            }
            RealmList<Pitch> friday = cur.friday;
            for (int j = 0; j < friday.size(); j++) {
                Pitch cur2 = friday.get(j);
                cur2.deleteFromRealm();
            }
            RealmList<Pitch> saturday = cur.saturday;
            for (int j = 0; j < saturday.size(); j++) {
                Pitch cur2 = saturday.get(j);
                cur2.deleteFromRealm();
            }
        }
        peaky.phase.deleteAllFromRealm();
        peaky.deleteFromRealm();
        realm.commitTransaction();
        Snackbar.make(v, "Peak Deleted!", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Intent i = new Intent(peak_phases.this, YourPeaks.class);
        startActivity(i);
    }
}
