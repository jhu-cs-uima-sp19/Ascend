package com.example.ascend;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class createPeak extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private TextView mTextMessage;

    private Button s;
    private Button e;
    String n;
    String d;
    private boolean fromStart;
    private TextView startDate;
    private TextView endDate;
    private Date peak_start_date;
    private Date peak_end_date;
    private Button add_phase;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent i = new Intent(createPeak.this, HomePage.class);
                    startActivity(i);
                case R.id.navigation_peaks:
                    return true;
                case R.id.navigation_browse:
                    Intent intent = new Intent(createPeak.this, BrowseFirstPage.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_peak);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setItemIconTintList(null);
        Realm realm = Realm.getDefaultInstance();
        final TextView name = (TextView) findViewById(R.id.peak_name);
        final TextView desc = (TextView) findViewById(R.id.peak_description);
        final Button done = (Button) findViewById(R.id.save);
        final Button cancel = (Button) findViewById(R.id.cancel);
        startDate = findViewById(R.id.starter);
        endDate = findViewById(R.id.ender);
        Intent i = getIntent();
        final boolean peakSet = i.getBooleanExtra("peakSet", false);
        if (peakSet) {
            n = i.getStringExtra("peakname");
            Peak curPeak = realm.where(Peak.class).equalTo("name", n).findFirst();
            d = curPeak.description;
            peak_start_date = curPeak.start;
            peak_end_date = curPeak.end;
            String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(peak_start_date);
            startDate.setText(currentDateString);
            currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(peak_end_date);
            endDate.setText(currentDateString);
            name.setText(n);
            desc.setText(d);
        }

        //Set initial dates to today and some set future date
        /*
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(peak.start);
        startDate.setText(currentDateString);
        currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(peak.end);
        endDate.setText(currentDateString);
        */

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                n = (String) name.getText().toString();
                d = (String) desc.getText().toString();
                if (peakSet) {
                    Intent i = new Intent(createPeak.this, YourPeaks.class);
                    startActivity(i);
                    finish();
                }

                else if (n.length() == 0) {
                    Snackbar.make(view, "Need a name!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
                else if (d.length() == 0) {
                    Snackbar.make(view, "Need a description!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if (startDate.getText().length() == 0 || endDate.getText().length() == 0) {
                    Snackbar.make(view, "Need dates!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                else if (peak_start_date.compareTo(peak_end_date) < 0) {
                    Peak p = new Peak(n, d, peak_start_date, peak_end_date, false); //fix the date here
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    Peak marathon = realm.where(Peak.class).equalTo("name", n).findFirst();
                    if (marathon == null) {
                        realm.copyToRealm(p);
                        realm.commitTransaction();
                        Intent i = new Intent(createPeak.this, HomePage.class);
                        startActivity(i);
                    } else {
                        Snackbar.make(view, "No duplicate peaks!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        realm.commitTransaction();
                    }
                } else {
                    Snackbar.make(view, "Start date cannot be after end date!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        s = findViewById(R.id.start_date);

        s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                fromStart = true;
            }
        });

        e = findViewById(R.id.end_date);

        e.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                fromStart = false;
            }
        });

        add_phase = findViewById(R.id.add_phase_button);

        add_phase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                n = (String) name.getText().toString();
                d = (String) desc.getText().toString();

                if (n.length() == 0) {
                    Snackbar.make(v, "Need a name!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
                else if (d.length() == 0) {
                    Snackbar.make(v, "Need a description!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if (startDate.getText().length() == 0 || endDate.getText().length() == 0) {
                    Snackbar.make(v, "Need dates!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else if (peak_start_date.compareTo(peak_end_date) < 0) {
                        Peak p = new Peak(n, d, peak_start_date, peak_end_date, false); //fix the date here
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        Peak marathon = realm.where(Peak.class).equalTo("name", n).findFirst();
                        if (marathon == null) {
                            realm.copyToRealm(p);
                            realm.commitTransaction();
                            Intent i = new Intent(createPeak.this, CreatePeak_AddPhases.class);
                            i.putExtra("start", p.start);
                            i.putExtra("end", p.end);
                            i.putExtra("peakname", n);
                            startActivity(i);
                        }
                        else {
                            Snackbar.make(v, "No duplicate peaks!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                } else {
                    Snackbar.make(v, "Start date cannot be after end date!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.MEDIUM).format(c.getTime());

        //Log.d(TAG, "onDateSet: " + view.getId());

        if (fromStart) {
            //Log.d(TAG, "onDateSet: in here");
            startDate = findViewById(R.id.starter);
            startDate.setText(currentDateString);
            peak_start_date = c.getTime();
        }
        else {
            //Log.d(TAG, "onDateSet: in there");
            endDate = findViewById(R.id.ender);
            endDate.setText(currentDateString);
            peak_end_date = c.getTime();
        }
    }
}
