package com.example.testedit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstBlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstBlankFragment extends Fragment {
    private static final String TAG = "F BlankFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private Button mButton;
    private Button mButtonA;
    private Button mButtonB;

    public FirstBlankFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BlankFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstBlankFragment newInstance(String param1, String param2) {
        FirstBlankFragment fragment = new FirstBlankFragment();
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
        Log.d(TAG, "onAttach: ");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_first_blank, container, false);
        mButton = v.findViewById(R.id.test_btn);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getFragmentManager().popBackStack();
                Log.e(TAG, "getFragmentManager: " + getFragmentManager());
                FragmentTransaction transaction = getFragmentManager().beginTransaction();

                // test add
                //transaction.add(R.id.test_content, BlankFragment.newInstance("12", "34"));

                // test replace
                //transaction.replace(R.id.test_content, BlankFragment.newInstance("12", "34"));

                // test remove
//                List<Fragment> list = getFragmentManager().getFragments();
//                for (Fragment f : list) {
//                    transaction.remove(f);
//                }

                // test popBackStack
                // getFragmentManager().popBackStack();

                transaction.add(R.id.test_content, BlankFragment.newInstance("12", "34"));
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });
        mButtonA = v.findViewById(R.id.test_btn_activity);
        mButtonA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity2.class);
                getActivity().startActivity(intent);
            }
        });
        mButtonB = v.findViewById(R.id.test_btn_activity3);
        mButtonB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity3.class);
                getActivity().startActivity(intent);
            }
        });
        Log.d(TAG, "onCreateView: ");
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    /**
     * Fragment A adds(not replace) fragment B, fragment B return(not remove fragment A) to
     * fragment A, onResume and onStart are not called.
     *
     * onResume, onStart are only call when if this fragment is loaded firstly, or it is removed or
     * destroyed then recreate.
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        /*InputMethodManager ime = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        ime.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS);*/

        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams
        // .SOFT_INPUT_STATE_VISIBLE);
    }

    /* (non-Javadoc)
     * @see android.app.Activity#dispatchTouchEvent(android.view.MotionEvent)
     */

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged: " + hidden);
        /*只在使用fragment show和hide接口来显示和隐藏fragment时，这个接口才会被调用到。*/
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: ");
        mButton = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
    }
}