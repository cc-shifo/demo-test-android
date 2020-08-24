package com.example.demopainter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PainterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PainterFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private Context mContext;
    private CompositeDisposable mDisposable;
    private Painter mPainter;
    private String mWatermark;

    public PainterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PainterFragment.
     */
    public static PainterFragment newInstance(String param1, String param2) {
        PainterFragment fragment = new PainterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_painter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(@NonNull View view) {
        mPainter = view.findViewById(R.id.fragment_signature_painter);
        Button btnCancel = view.findViewById(R.id.fragment_signature_btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPainter.cleanPaint();
                //saveBitMap();
            }
        });
        Button btnClean = view.findViewById(R.id.fragment_signature_btn_clean);
        btnClean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPainter.cleanPaint();
            }
        });
        Button btnOK = view.findViewById(R.id.fragment_signature_btn_confirm);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPainter.isEmptyPainting()) {
                    Toast.makeText(mContext, "请签名", Toast.LENGTH_LONG).show();
                } else {
                    saveBitMap();
                }
            }
        });
    }

    private void saveBitMap() {
        Observable.create(new ObservableOnSubscribe<Bitmap>() {
                    @Override
                    public void subscribe(@io.reactivex.rxjava3.annotations.NonNull ObservableEmitter<Bitmap> emitter) throws Throwable {
                        mPainter.setWatermark("0011223344");
                        mPainter.setBrushColor(Color.rgb(0xFF, 0xFF, 0));
                        Bitmap bitmap = mPainter.getCropPaintingWithWaterMark(384, 200);
                        if (!emitter.isDisposed()) {
                            if (bitmap == null) {
                                emitter.onError(new Throwable("null bitmap"));
                            } else {
                                emitter.onNext(bitmap);
                                emitter.onComplete();
                            }
                        }
                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
                        .observeOn(Schedulers.io())
                        .subscribe(new Observer<Bitmap>() {
                            @Override
                            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                                mDisposable.add(d);
                            }

                            @Override
                            public void onNext(@io.reactivex.rxjava3.annotations.NonNull Bitmap bitmap) {
                                boolean result = mPainter.saveImg(bitmap);
                                if (result) {
                                    LogUtils.d("save successfully");
                                } else {
                                    LogUtils.d("save failed");
                                }
                            }

                            @Override
                            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                                LogUtils.e(e.toString());
                            }

                            @Override
                            public void onComplete() {

                            }
                        }
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.clear();
        }
    }
}