package com.demo.myhttp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ApiRvAdapter extends RecyclerView.Adapter<ApiRvAdapter.RVHolder> {
    private final Context mContext;
    @LayoutRes
    private final int mItemLayoutId;
    private final List<String> mItems = new ArrayList<>();
    private OnItemClick mOnItemClick;

    public ApiRvAdapter(@NonNull RecyclerView rv, @LayoutRes int itemLayoutId, List<String> items) {
        mContext = rv.getContext();
        mItemLayoutId = itemLayoutId;
        mItems.addAll(items);
    }

    public List<String> getItems() {
        return mItems;
    }

    public void setItems(List<String> items) {
        mItems.clear();
        mItems.addAll(items);
    }

    public void setOnItemClick(OnItemClick onItemClick) {
        mOnItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ApiRvAdapter.RVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mItemLayoutId, parent, false);
        return new ApiRvAdapter.RVHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApiRvAdapter.RVHolder holder, final int position) {
        holder.getTextView().setText(mItems.get(position));
        holder.getItem().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClick != null) {
                    mOnItemClick.click(position);
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
        return mItems.size();
    }

    public static class RVHolder extends RecyclerView.ViewHolder {
        private final TextView mTextView;
        private final View mItem;
        public RVHolder(@NonNull View itemView) {
            super(itemView);
            mItem = itemView.findViewById(R.id.rv_item);
            mTextView = itemView.findViewById(R.id.tv_rv_api_name);
        }

        public TextView getTextView() {
            return mTextView;
        }

        public View getItem() {
            return mItem;
        }
    }

    public interface OnItemClick {
        public void click(int itemId);
    }
}
