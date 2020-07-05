package com.example.demo.demobottomsheetdialogfragment.home;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.demo.demobottomsheetdialogfragment.R;

import java.util.ArrayList;
import java.util.List;

public class HomeMenuRecyclerAdapter extends RecyclerView.Adapter<HomeMenuRecyclerAdapter.ItemVH> {
    List<HomeMenuItemData> mDataList = new ArrayList<>();
    ItemClickListener mClickListener;

    public HomeMenuRecyclerAdapter(@NonNull List<HomeMenuItemData> dataList) {
        mDataList.addAll(dataList);
//        mDataList = dataList;
    }

    @NonNull
    @Override
    public ItemVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout
                .activity_home_menu_rv_item_layout, viewGroup, false);
        return new ItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemVH viewHolder, int i) {
        final HomeMenuItemData itemData = mDataList.get((int)getItemId(i));
        viewHolder.mTextView.setText(itemData.getLabel());
        viewHolder.mImageView.setImageResource(itemData.getImageResID());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mClickListener != null) {
                    mClickListener.onClick(itemData);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    public static class ItemVH extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTextView;

        public ItemVH(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.home_menu_rv_item_icon);
            mTextView = itemView.findViewById(R.id.home_menu_rv_item_label);
        }
    }

    public void setDataList(List<HomeMenuItemData> dataList) {
        mDataList = dataList;
    }

    public void setClickListener(ItemClickListener clickListener) {
        mClickListener = clickListener;
    }

    public interface ItemClickListener {
        void onClick (HomeMenuItemData item);
    }
}
