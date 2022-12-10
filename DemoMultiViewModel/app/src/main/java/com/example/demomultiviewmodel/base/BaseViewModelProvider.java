package com.example.demomultiviewmodel.base;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BaseViewModelProvider extends ViewModelProvider {
    public BaseViewModelProvider(@NonNull ViewModelStoreOwner owner) {
        super(owner);
    }

    public BaseViewModelProvider(@NonNull ViewModelStoreOwner owner, @NonNull Factory factory) {
        this(owner.getViewModelStore(), factory);
    }

    public BaseViewModelProvider(@NonNull ViewModelStore store, @NonNull Factory factory) {
        super(store, factory);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T get(@NonNull Class<T> modelClass) {
        return super.get(modelClass);
    }

    @NonNull
    @Override
    public <T extends ViewModel> T get(@NonNull String key, @NonNull Class<T> modelClass) {
        ViewModelStore modelStore = null;
        ViewModel viewModel = null;
        Class<?> supperClass = this.getClass().getSuperclass();
        try {
            assert supperClass != null;
            Field field = supperClass.getDeclaredField("mViewModelStore");
            field.setAccessible(true);
            Object obj = field.get(this);
            if (obj instanceof ViewModelStore) {
                modelStore = (ViewModelStore) obj;
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }


        if (modelStore == null) {
            throw new RuntimeException("Cannot create an instance of " + modelClass);
        }

        //noinspection TryWithIdenticalCatches
        try {
            Method mdGet = modelStore.getClass().getDeclaredMethod("get", String.class);
            mdGet.setAccessible(true);
            viewModel = (ViewModel) mdGet.invoke(modelStore, key);
            if (modelClass.isInstance(viewModel)) {
                return (T) viewModel;
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }

        viewModel = BaseViewModelFactory.getInstance().create(modelClass, modelStore);
        //noinspection TryWithIdenticalCatches
        try {
            Method mdPut = modelStore.getClass().getDeclaredMethod("put", String.class,
                    ViewModel.class);
            mdPut.setAccessible(true);
            mdPut.invoke(modelStore, key, viewModel);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }

        return (T) viewModel;
    }
}
