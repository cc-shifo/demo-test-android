
package com.example.demoimmerse;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.zhd.hiair.modulemoresetting.data.TabItemData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * more setting view model
 */
public class MoreSettingViewModel extends ViewModel {
    /**
     * TabLayout的icon数据。
     */
    private final MutableLiveData<List<TabItemData>> mTabItemDataList;
    private Disposable mDisLoadItem;

    public MoreSettingViewModel() {
        mTabItemDataList = new MutableLiveData<>();
    }

    /**
     * 监听本地tab item的数据。
     */
    @NonNull
    public LiveData<List<TabItemData>> observeTabItems() {
        return mTabItemDataList;
    }

    /**
     * 加载tab item数据。
     */
    public void loadTabItems(@NonNull final Context context) {
        mDisLoadItem = Observable.create((ObservableOnSubscribe<List<TabItemData>>) emitter -> {
                    List<TabItemData> itemICs = getLocalTabIcons(context);
                    if (!itemICs.isEmpty() && !emitter.isDisposed()) {
                        emitter.onNext(itemICs);
                        emitter.onComplete();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(mTabItemDataList::setValue, Timber::e);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (mDisLoadItem != null && !mDisLoadItem.isDisposed()) {
            mDisLoadItem.dispose();
        }
    }

    /**
     * Get icons for tabs of TabLayout from local xml file.
     *
     * @return icons for tabs of TabLayout.
     */
    @NonNull
    private List<TabItemData> getLocalTabIcons(@NonNull Context context) {
        List<TabItemData> itemICs = new ArrayList<>(0);
        Resources res = context.getResources();
        TypedArray icons = null;
        try {
            icons = res.obtainTypedArray(R.array.tab_item_icons);
            for (int i = 0; i < icons.length(); i++) {
                Drawable drawable = icons.getDrawable(i);
                if (drawable != null) {
                    itemICs.add(new TabItemData(drawable));
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (icons != null) {
                icons.recycle();
            }
        }

        return itemICs;
    }
}
