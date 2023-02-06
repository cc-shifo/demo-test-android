package com.example.demospinner;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DemoAdapter1 extends ArrayAdapter<String> {
    private static final int NO_POSITION = -1;
    private Context mContext;
    private int mPosition = NO_POSITION;

    public DemoAdapter1(@NonNull Context context, int resource,
                        @NonNull List<String> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView textView = (TextView)view.findViewById(android.R.id.text1);
        textView.setGravity(Gravity.CENTER);
        if (mPosition == position) {
            textView.setTextColor(mContext.getResources().getColor(R.color
                    .selected));
        }
        return view;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        TextView textView = (TextView)view.findViewById(android.R.id.text1);
        textView.setGravity(Gravity.CENTER);
        return view;
    }

    public void select(int position) {
        mPosition = position;
    }
}
