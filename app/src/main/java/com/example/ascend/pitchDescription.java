package com.example.ascend;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import io.realm.Realm;
import io.realm.RealmResults;

public class pitchDescription extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    TextView name;
    TextView plan;
    String format;
    String start1;
    String end1;
    String pitchName;
    String peakname;
    String phasename;
    Pitch pitch;
    Button butt;
    Button butt2;
    int hour;
    private boolean fromstart;
    int min;
    String[] days = {"Su", "M", "T", "W", "Th", "F", "S"};

    private static final String TAG = "pitchDescription";
    SharedPreferences sharedpreferences;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent i = new Intent(pitchDescription.this, HomePage.class);
                    startActivity(i);
                    return true;
                case R.id.navigation_peaks:
                    return true;
                case R.id.navigation_browse:
                    Intent intent = new Intent(pitchDescription.this, BrowseFirstPage.class);
                    startActivity(intent);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pitch_description);
        Intent i = getIntent();
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_peaks);
        navigation.setItemIconTintList(null);
        peakname = i.getStringExtra("peakname");
        pitchName = i.getStringExtra("pitchName");
        phasename = i.getStringExtra("phasename");
        butt = (Button) findViewById(R.id.button);
        RealmResults<Pitch> realmPitch = realm.where(Pitch.class).equalTo("name", pitchName).findAll();
        pitch = realmPitch.get(0);
        EditText et = (EditText) findViewById(R.id.Notes);
        et.setText(pitch.plan);
        name = findViewById(R.id.Name);
        name.setText("Peak: " + peakname + "\n" + pitch.start + " - " + pitch.end);
        plan = findViewById(R.id.textView2);
        plan.setText("Plan");
        butt2 = (Button) findViewById(R.id.button2);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker1 = new TimePickerFragment();
                fromstart = true;
                timePicker1.show(getSupportFragmentManager(), "time picker");
            }
        });

        butt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timepicker2 = new TimePickerFragment();
                fromstart = false;
                timepicker2.show(getSupportFragmentManager(), "time picker2");
            }
        });
        Button butt3 = (Button) findViewById(R.id.button3);
        butt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Plan saved!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                saveChanges();
            }
        });

        final Button delete = (Button) findViewById(R.id.deletePitch);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Pitch Deleted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                deletePitch(delete);
            }
        });

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        name = findViewById(R.id.Name);
        if (hourOfDay == 0) {
            hourOfDay += 12;
            format = "AM";
        } else if (hourOfDay == 12) {
            format = "PM";
        } else if (hourOfDay > 12) {
            hourOfDay -= 12;
            format = "PM";
        } else {
            format = "AM";
        }
        if (fromstart == true) {
            start1 = String.format("%02d:%02d", hourOfDay, minute);
            start1 = start1 + format;
            //name.setText("Peak: " + peakname + "\n" + new StringBuilder().append(hourOfDay).append(" : ").append(minute).append(" ").append(format));
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            pitch.start = start1;
            realm.commitTransaction();
            name.setText("Peak: " + peakname + "\n" + pitch.start + " - " + pitch.end);
            Snackbar.make(butt, "Time changed!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else if (fromstart == false) {
            end1 = String.format("%02d:%02d", hourOfDay, minute);
            end1 = end1 + format;
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            pitch.end = end1;
            realm.commitTransaction();
            //name.setText("Peak: " + peakname + "\n" + new StringBuilder().append(hourOfDay).append(" : ").append(minute).append(" ").append(format));
            name.setText("Peak: " + peakname + "\n" + pitch.start + " - " + pitch.end);
            Snackbar.make(butt2, "Time changed!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

        }
    }

    protected void saveChanges() {
        EditText edit = (EditText) findViewById(R.id.Notes);
        String message = edit.getText().toString();
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        pitch.plan = message;
        realm.commitTransaction();
        Intent i = new Intent(pitchDescription.this, phasetasks.class);
        i.putExtra("peakname", peakname);
        i.putExtra("phasename", phasename);
        startActivity(i);
    }

    protected void deletePitch(View v) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Pitch pitchy = realm.where(Pitch.class).equalTo("name", pitchName).findFirst();
        pitchy.deleteFromRealm();
        realm.commitTransaction();
        Snackbar.make(v, "Pitch Deleted!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
        Intent i = new Intent(pitchDescription.this, phasetasks.class);
        i.putExtra("peakname", peakname);
        i.putExtra("phasename", phasename);
        startActivity(i);
    }
}
