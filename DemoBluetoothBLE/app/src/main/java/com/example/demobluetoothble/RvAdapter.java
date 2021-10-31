package com.example.demobluetoothble;

import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.VH> {

    private List<BluetoothDevice> mDevices;

    public RvAdapter(List<BluetoothDevice> devices) {
        mDevices = devices;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent,
                                 int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RvAdapter.VH holder,
                                 int position) {

    }

    @Override
    public int getItemCount() {
        return mDevices == null ? 0 : mDevices.size();
    }

    public List<BluetoothDevice> getDevices() {
        return mDevices;
    }

    public void setDevices(List<BluetoothDevice> devices) {
        mDevices = devices;
    }

    protected static class VH extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mAddress;

        public VH(@NonNull View itemView) {
            super(itemView);
        }
    }
}
