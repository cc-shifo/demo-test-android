package com.example.demomultiviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class AppViewModelFactory implements ViewModelProvider.Factory {
    private static AppViewModelFactory sInstance;

    /**
     * Retrieve a singleton instance of NewInstanceFactory.
     *
     * @return A valid {@link ViewModelProvider.NewInstanceFactory}
     */
    @NonNull
    public static AppViewModelFactory getInstance() {
        if (sInstance == null) {
            sInstance = new AppViewModelFactory();
        }
        return sInstance;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        T viewModel;
        if (MainViewModel.class.equals(modelClass)) {
            viewModel = newInstance(modelClass);
        }

        return null;
    }


    private <T extends ViewModel> T newInstance(@NonNull Class<T> modelClass) {
        //noinspection TryWithIdenticalCatches
        try {
            return modelClass.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }

    private <T extends ViewModel> T newInstance(@NonNull Class<T> modelClass,
                                                Class<?>... parameterTypes) {


        //noinspection TryWithIdenticalCatches
        try {
            return constructor.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private <T extends ViewModel> Constructor<T> getConstructor(@NonNull Class<T> modelClass,
                                                                Class<?>... parameterTypes) {
        try {
            return modelClass.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
