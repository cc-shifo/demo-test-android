package com.example.demoh5jsnativecomm.fileexplorer;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.demoh5jsnativecomm.R;
import com.example.demoh5jsnativecomm.utils.FileExploreUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FileIEViewModel extends AndroidViewModel {
    private static final String TAG = "FileIEViewModel";
    private final MutableLiveData<List<ItemData>> mDirList;
    private Disposable mDisposable;

    public FileIEViewModel(@NonNull Application application) {
        super(application);
        mDirList = new MutableLiveData<>();
    }

    public LiveData<List<ItemData>> observeFileList() {
        return mDirList;
    }

    /**
     * 加载{@code selectedItem}目录下的文件
     *
     * @param selectedItem 选中的目录
     */
    public void loadFileList(@NonNull final File selectedItem) {
        mDisposable = Observable.create((ObservableOnSubscribe<List<File>>) emitter -> {
            List<File> fileList = open(selectedItem);


            if (!emitter.isDisposed()) {
                emitter.onNext(fileList);
                emitter.onComplete();
            }
        }).map(new Function<List<File>, List<ItemData>>() {
            @Override
            public List<ItemData> apply(List<File> files) throws Throwable {
                List<ItemData> itemDataList = new ArrayList<>();
                for (File file : files) {
                    itemDataList.add(new ItemData(file, renderItem(file)));
                }

                if (!selectedItem.getPath().equals(Environment.getExternalStorageDirectory().getPath())) {
                    itemDataList.add(0, new ItemData(
                            selectedItem.getParentFile(), renderParentLink()));
                }

                return itemDataList;
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(files -> mDirList.setValue(files), throwable -> Log.e(TAG, "accept: "
                        , throwable));
    }

    private List<File> open(@NonNull File selectedItem) {
        return FileExploreUtil.listFilesInDirWithFilter(selectedItem,
                pathname -> !pathname.isHidden(), false, null);
    }

    // public List<File> getFilesList(File selectedItem) {
    //     File[] rawFilesArray = selectedItem.listFiles(pathname -> !pathname.isHidden());
    //
    //     return Arrays.asList(rawFilesArray != null ? rawFilesArray : new File[0]);
    // }

    private String renderParentLink() {
        return getApplication().getString(R.string.go_parent_label);
    }

    private String renderItem(File file) {
        if (file.isDirectory()) {
            return getApplication().getString(R.string.folder_item, file.getName());
        } else {
            return getApplication().getString(R.string.file_item, file.getName());
        }
    }

    public void destroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
    }
}
