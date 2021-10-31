package com.example.adapterposition;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapterposition.databinding.ItemViewBinding;

import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.VH> {
    private static final String TAG = "RvAdapter";
    private final OnItemClick mOnItemClick;
    private List<Long> mList;
    private boolean mHasStableId;

    public RvAdapter(@NonNull OnItemClick onItemClick, boolean hasStableId) {
        mOnItemClick = onItemClick;
        mHasStableId = hasStableId;
        mHasStableId = true;
        setHasStableIds(mHasStableId);
    }

    @NonNull
    @Override
    public RvAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_view,
                parent, false);
        return new VH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RvAdapter.VH holder,
                                 final int position) {
        Log.d(TAG, "onBindViewHolder: " + position);
        holder.mBinding.tittle.setText(String.valueOf(mList.get(position)));
        holder.mBinding.btn.setText(mHasStableId ? "stable" : "not");
        holder.mBinding.btn.setOnClickListener(v -> {
            mOnItemClick.onClick(position);
            mOnItemClick.directQueryAdapter(holder.getAdapterPosition());
        });
    }

    @Override
    public long getItemId(int position) {
        if (!mHasStableId) {
            return RecyclerView.NO_ID;
        }
        Log.d(TAG, "getItemId: " + position);
        mOnItemClick.adapterPosition(position);
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public List<Long> getList() {
        return mList;
    }

    public void setList(@NonNull List<Long> list) {
        if (mList == null) {
            mList = list;
            notifyItemRangeInserted(0, mList.size());
        } else {
            DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mList.size();
                }

                @Override
                public int getNewListSize() {
                    return list.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mList.get(oldItemPosition).equals(mList.get(newItemPosition));

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    return mList.get(oldItemPosition).equals(mList.get(newItemPosition));
                }
            });
            diffResult.dispatchUpdatesTo(this);
            mList = list;
        }
    }

    public interface OnItemClick {
        void onClick(int position);

        void directQueryAdapter(int position);

        void adapterPosition(int position);
    }

    static class VH extends RecyclerView.ViewHolder {
        private final ItemViewBinding mBinding;

        public VH(@NonNull ItemViewBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
