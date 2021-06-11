package com.example.sliderecyclerview;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    List<String> mList = new ArrayList<>();

    public MyAdapter(List<String> list) {
        mList = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent
                , false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String name = mList.get(position);
        Uri uri = null;
        Glide.with(holder.itemView.getContext())
                .load(uri)
                .error(R.drawable.activity_download_rv_item_ic)
                .into(holder.mImageView);
        // holder.mImageView.setImageResource(R.drawable.activity_download_rv_item_ic);
        holder.mTextView.setText(name);
        holder.mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "hello", BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
        holder.mBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mList.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTextView;
        private Button mBtnDownload;
        private Button mBtnDelete;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.activity_download_rv_item_ic);
            mTextView = itemView.findViewById(R.id.tittle);
            mBtnDownload = itemView.findViewById(R.id.btn_download);
            mBtnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
