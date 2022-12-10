package com.example.demomultiviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

public class BaseViewModelProvider extends ViewModelProvider {
    private ViewModelStoreOwner mOwner;

    public BaseViewModelProvider(@NonNull ViewModelStoreOwner owner) {
        super(owner);
        mOwner = owner;
    }

    public BaseViewModelProvider(@NonNull ViewModelStoreOwner owner, @NonNull Factory factory) {
        this(owner.getViewModelStore(), factory);
        mOwner = owner;
    }

    public BaseViewModelProvider(@NonNull ViewModelStore store, @NonNull Factory factory) {
        super(store, factory);
    }
}
