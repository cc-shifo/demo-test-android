package com.example.demospinner;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class DemoAdapter0 extends ArrayAdapter<String> {
    private static final int NO_POSITION = -1;
    private Context mContext;
    private int mPosition = NO_POSITION;

    public DemoAdapter0(@NonNull Context context, int resource,
                        @NonNull List<String> objects) {
        super(context, resource, objects);
        mContext = context;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        final View view;
        final TextView text;

        if (convertView == null) {
            view = LayoutInflater.from(mContext)
                    .inflate(android.R.layout.simple_spinner_item, parent, false);
        } else {
            view = convertView;
        }
        text = view.findViewById(android.R.id.text1);
        final String item = getItem(position);
        text.setText(item);
        text.setGravity(Gravity.CENTER);
        text.setTextColor(mContext.getResources().getColor(R.color.drop_down_text_item_selector));
        if (mPosition == position) {
            text.setSelected(true);
        }

        return view;
    }

    public void select(int position) {
        mPosition = position;
    }
}
