package com.example.ascend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

public class RecyclerViewAdapterPitch  extends  RecyclerView.Adapter<RecyclerViewAdapterPitch.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapterPitc";

    private ArrayList<Pitch> pitches;
    private Context nContext;

    public RecyclerViewAdapterPitch(ArrayList<Pitch> p, Context c) {
        pitches = p;
        nContext = c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.phasetasks_layout, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called");
        final Pitch p = pitches.get(i);
        viewHolder.name.setText(p.getName());
        viewHolder.time.setText(p.getStart() + "-" + p.getEnd());
    }


    @Override
    public int getItemCount() {
        return pitches.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Button name;
        Button time;
        ConstraintLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            time = itemView.findViewById(R.id.time);
            layout = itemView.findViewById(R.id.pitch_layout);
        }
    }

}