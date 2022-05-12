package com.example.demoh5jsnativecomm.fileexplorer;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demoh5jsnativecomm.R;
import com.example.demoh5jsnativecomm.databinding.FileExplorerRvItemBinding;

import java.io.File;
import java.util.List;

public class FileExplorerRvAdapter extends RecyclerView.Adapter<FileExplorerRvAdapter.FileIEVH> {
    private List<ItemData> mList;
    private ItemOnClick mItemOnClick;

    @NonNull
    @Override
    public FileIEVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FileExplorerRvItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.file_explorer_rv_item,
                parent, false);

        return new FileIEVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FileIEVH holder, int position) {
        String name = mList.get(holder.getAdapterPosition()).getItemName();
        holder.mBinding.tvName.setText(name);
        holder.mBinding.tvName.setOnClickListener(v -> {
            if (mItemOnClick != null) {
                mItemOnClick.onClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public void setList(@Nullable List<ItemData> list) {
        mList = list;
    }

    public void setItemOnClick(@Nullable ItemOnClick itemOnClick) {
        mItemOnClick = itemOnClick;
    }

    public interface ItemOnClick {
        void onClick(int position);
    }

    public static class FileIEVH extends RecyclerView.ViewHolder {
        private final FileExplorerRvItemBinding mBinding;

        public FileIEVH(@NonNull FileExplorerRvItemBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }

    private String renderParentLink(AppCompatActivity activity) {
        return activity.getString(R.string.go_parent_label);
    }

    private String renderItem(AppCompatActivity activity, File file) {
        if (file.isDirectory()) {
            return activity.getString(R.string.folder_item, file.getName());
        } else {
            return activity.getString(R.string.file_item, file.getName());
        }
    }
}
