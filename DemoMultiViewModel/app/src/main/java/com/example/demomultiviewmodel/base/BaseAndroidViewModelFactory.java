package com.example.demomultiviewmodel.base;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

public class BaseAndroidViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private static BaseAndroidViewModelFactory sInstance;
    private Application mApplication;

    /**
     * Retrieve a singleton instance of AndroidViewModelFactory.
     *
     * @param application an application to pass in {@link AndroidViewModel}
     * @return A valid {@link BaseAndroidViewModelFactory}
     */
    @NonNull
    public static BaseAndroidViewModelFactory getInstance(@NonNull Application application) {
        if (sInstance == null) {
            sInstance = new BaseAndroidViewModelFactory(application);
        }
        return sInstance;
    }


    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public BaseAndroidViewModelFactory(@NonNull Application application) {
        mApplication = application;
    }


    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return create(modelClass, null);
    }

    public <T extends ViewModel> T create(@NonNull Class<T> modelClass,
                                          @Nullable ViewModelStoreOwner owner) {
        // boolean isAndroidViewModel = AndroidViewModel.class.isAssignableFrom(modelClass);
        // if (!isAndroidViewModel) {
        //     throw new RuntimeException("Cannot create an instance of " + modelClass);
        // }

        // return createInstance(modelClass, owner);
        return newInstance(modelClass, owner);
    }


    // 方法一，循环找构造函数，来创建对象。
    private static final Class<?>[] NESTED_ANDROID_VIEW_MODEL_SIGNATURE =
            new Class[]{Application.class,
                    ViewModelStoreOwner.class};

    private static final Class<?>[] ANDROID_VIEW_MODEL_SIGNATURE = new Class[]{Application.class};

    private <T extends ViewModel> T createInstance(@NonNull Class<T> modelClass,
                                                   @Nullable ViewModelStoreOwner owner) {
        Class<?>[] signature = owner == null ? ANDROID_VIEW_MODEL_SIGNATURE
                : NESTED_ANDROID_VIEW_MODEL_SIGNATURE;
        //noinspection TryWithIdenticalCatches
        try {
            Constructor<T> constructor = findMatchingConstructor(modelClass, signature);
            if (owner == null) {
                return Objects.requireNonNull(constructor).newInstance(mApplication);
            } else {
                return Objects.requireNonNull(constructor).newInstance(mApplication, owner);
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
                                                @Nullable ViewModelStoreOwner owner) {
        //noinspection TryWithIdenticalCatches
        try {
            Constructor<T> constructor;
            if (owner == null) {
                constructor = modelClass.getConstructor(Application.class);
                return constructor.newInstance(mApplication);
            } else {
                constructor = modelClass.getConstructor(Application.class,
                        ViewModelStoreOwner.class);
                return constructor.newInstance(mApplication, owner);
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
