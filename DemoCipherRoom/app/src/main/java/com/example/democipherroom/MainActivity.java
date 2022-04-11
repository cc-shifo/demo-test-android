package com.example.democipherroom;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.democipherroom.databinding.ActivityMainBinding;
import com.example.democipherroom.db.DemoDB;
import com.example.democipherroom.db.dao.PersonDao;
import com.example.democipherroom.db.entry.Person;
import com.example.democipherroom.db.entry.ProjectionPersonName;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
    /**
     * 外部存储器读写权限请求码
     */
    private static final int REQ_EXTERNAL_STORAGE = 100;
    private ActivityMainBinding mMainBinding;

    private boolean mHasPermissions;
    private final String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Disposable mDisposable;
    private PersonDao mPersonDao;
    private boolean mIsInsertBusy;
    private Disposable mInsertDisposable;
    private boolean mIsQueryBusy;
    private Disposable mQueryDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        checkSelfPermissions();
        initData();
        initView();
    }


    private void checkSelfPermissions() {
        mHasPermissions = false;
        boolean granted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager
                    .PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, permissions, REQ_EXTERNAL_STORAGE);
                granted = false;
                break;
            }
        }

        mHasPermissions = granted;
        if (mHasPermissions) {
            initDB();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_EXTERNAL_STORAGE) {

            if (permissions.length == 2 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mHasPermissions = true;
                initDB();
            } else {
                Toast.makeText(MainActivity.this, R.string.no_read_write_permission,
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initData() {
        mPersonDao = null;
    }


    private void initView() {
        mMainBinding.btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPersonDao != null && !mIsInsertBusy) {
                    mIsInsertBusy = true;
                    final Person person = new Person(1,
                            String.valueOf(SystemClock.elapsedRealtime()), 30, 1);
                    mInsertDisposable = mPersonDao.insertPerson(person)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                mMainBinding.txtPersonInsertInfo.setText(person.toString());
                                mIsInsertBusy = false;
                            }, throwable -> {
                                mMainBinding.txtPersonInsertInfo.setText(throwable.toString());
                                mIsInsertBusy = false;
                            });
                }
            }
        });

        mMainBinding.btnQuery.setOnClickListener(view -> {
            if (mPersonDao != null && !mIsQueryBusy) {
                mIsQueryBusy = true;
                mQueryDisposable = Observable.create((ObservableOnSubscribe<String>) emitter -> {
                    List<Person> list = mPersonDao.getAllPersons();
                    List<ProjectionPersonName> names = mPersonDao.getAllName();
                    StringBuilder builder = new StringBuilder();
                    for (Person person : list) {
                        builder.append("[").append(person.getId()).append(", ")
                                .append(person.getName()).append(", ")
                                .append(person.getAge()).append("]\n\n");
                    }
                    for (ProjectionPersonName name : names) {
                        builder.append("[").append(name.getName()).append("]\n\n");
                    }

                    Timber.d("btnQuery: %s", builder.toString());
                    if (!emitter.isDisposed()) {
                        emitter.onNext(builder.toString());
                        emitter.onComplete();
                    }
                }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws Throwable {
                                mMainBinding.txtPersonQueryInfo.setText(s);
                            }
                        }, throwable -> {
                            mMainBinding.txtPersonQueryInfo.setText(throwable.toString());
                            Timber.e(throwable);
                            mIsQueryBusy = false;
                        }, () -> mIsQueryBusy = false);
            }
        });
    }

    private void initDB() {
        mDisposable = Observable.create((ObservableOnSubscribe<Boolean>) emitter -> {
            DemoDB.init(MainActivity.this);
            if (!emitter.isDisposed()) {
                emitter.onNext(true);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Throwable {
                        mPersonDao = DemoDB.getInstance().getPersonDao();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Throwable {
                        mPersonDao = null;
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }
}