package com.example.demomultiviewmodel.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class BaseViewModelFactory implements ViewModelProvider.Factory {
    private static BaseViewModelFactory sInstance;
    private ViewModelProvider.Factory mFactory;

    /**
     * Retrieve a singleton instance of NewInstanceFactory.
     *
     * @return A valid {@link ViewModelProvider.NewInstanceFactory}
     */
    @NonNull
    public static BaseViewModelFactory getInstance() {
        if (sInstance == null) {
            sInstance = new BaseViewModelFactory();
        }
        return sInstance;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return create(modelClass, null);
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass,
                                          @Nullable ViewModelStore viewModelStore) {
        // boolean isAndroidViewModel = AndroidViewModel.class.isAssignableFrom(modelClass);
        // if (isAndroidViewModel) {
        //     throw new RuntimeException("Cannot create an instance of " + modelClass);
        // }

        // return createInstance(modelClass, owner);
        return newInstance(modelClass, viewModelStore);
    }


    // 方法一，循环找构造函数，来创建对象。
    private static final Class<?>[] NESTED_VIEW_MODEL_SIGNATURE =
            new Class[]{ViewModelStoreOwner.class};
    private static final Class<?>[] VIEW_MODEL_SIGNATURE = new Class[0];


    private <T extends ViewModel> T createInstance(@NonNull Class<T> modelClass,
                                                   @Nullable ViewModelStoreOwner owner) {
        Class<?>[] signature = owner == null ? VIEW_MODEL_SIGNATURE
                : NESTED_VIEW_MODEL_SIGNATURE;
        Constructor<T> constructor = findMatchingConstructor(modelClass, signature);
        assert constructor != null;
        try {
            if (owner == null) {
                return constructor.newInstance();
            } else {
                return constructor.newInstance(owner);
            }
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> Constructor<T> findMatchingConstructor(Class<T> modelClass,
                                                              Class<?>[] signature) {
        for (Constructor<?> constructor : modelClass.getConstructors()) {
            Class<?>[] parameterTypes = constructor.getParameterTypes();
            if (Arrays.equals(signature, parameterTypes)) {
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    // 方法二，根据固定入参调用固定参数的构造函数来创建对象。
    private <T extends ViewModel> T newInstance(@NonNull Class<T> modelClass,
                                                @Nullable ViewModelStore viewModelStore) {
        //noinspection TryWithIdenticalCatches
        try {
            if (viewModelStore == null) {
                return modelClass.newInstance();
            } else {
                Constructor<T> constructor = modelClass.getConstructor(ViewModelStore.class);
                return constructor.newInstance(viewModelStore);
            }
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
